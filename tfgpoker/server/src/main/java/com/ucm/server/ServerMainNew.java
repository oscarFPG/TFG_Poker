package com.ucm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.SocketChannelUtils;
import com.ucm.server.control.Controller;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;
import com.ucm.server.middleclasses.ClientStructGame;


public class ServerMainNew {
    

    private static final Logger log = LogManager.getLogger(ServerMainNew.class);

    private static List<ClientStructPreGame> _authPlayers = new ArrayList<>();     //  Players authenticathed(They sent their name) but not joined
    private static List<ClientStructPreGame> _roomPlayers = new ArrayList<>();     // Players in the game room

    private static ClientStructPreGame _host = null;

    public static boolean _waitingPlayersOnPregame = false;
    private static boolean _gameStarts = false;     // Flag to indicate if the game should start


    private static class ClientStructPreGame {

        public String clientName;
        public SocketChannel clientSocket;
        public SelectionKey key;

        ClientStructPreGame(final String name, final SocketChannel s, final SelectionKey k){
            clientName = name;
            clientSocket = s;
            key = k;
        }
    }


    public static void main(String[] args) {

        try {

            showServerIP();
            preGame();

            List<ClientStructGame> players = new ArrayList<>();
            for(ClientStructPreGame cs : _roomPlayers){
                cs.clientSocket.configureBlocking(true);
                players.add( new ClientStructGame(cs.clientName, cs.clientSocket.socket()) );
            }
            game(players);


        }
        catch(IOException | InterruptedException e) {
            log.fatal("Couldnt get the public server ip {}", e.getMessage());
            return;
        }


    }

    
    private static void showServerIP() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                                .newBuilder()
                                .uri(URI.create("https://api.ipify.org"))
                                .GET()
                                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String serverIP = response.body();
        log.debug("Server public IP obtained: {}", serverIP);
    } 


    private static void preGame() throws IOException {

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        Selector selector = Selector.open();

        serverSocket.configureBlocking(false);
        serverSocket.bind( new InetSocketAddress(GameType.PORT) );
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        while(!_gameStarts) {

            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {

                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid())
                    continue;

                try {

                    if (key.isAcceptable()) {
                        handleAccept(key, selector);
                    } 
                    if (key.isReadable()) {
                        handleReceive(key);
                    } 
                    if (key.isWritable()) {
                        handleSend(key);
                    }
                }
                catch(IOException e) {
                    closeClient(key);
                }
            }


        }
        log.debug("Game starts!");

        // Cancel all clients left keys -> Important!
        for (SelectionKey key : selector.keys()) {
            key.cancel();
        }
        selector.selectNow();

        // Send to all clients the GAME_STARTS flag
        ByteBuffer broadcastBuffer = ByteBuffer.allocate(Integer.BYTES);
        broadcastBuffer.putInt(GameType.GAME_STARTS);
        for(ClientStructPreGame cs : _roomPlayers){
            try {
                broadcastBuffer.rewind();
                cs.clientSocket.write(broadcastBuffer);
            }
            catch(IOException e) {
                log.error("Sending GAME_STARTS flag: {}", e.getMessage());
                try {
                    cs.clientSocket.close();
                }
                catch(IOException exception) {
                    log.error("Closing socket: {}", e.getMessage());
                }
            }
        }

        //  Eliminate all not in-game players to avoid infinite waiting
        for(ClientStructPreGame cs : _authPlayers){
            try {
                cs.key.cancel();
                cs.clientSocket.close();
            }
            catch(IOException ignored){}
        }

    }

    private static void game(List<ClientStructGame> clients) {

        try{
            Game game = new Game();
            Controller controller = new Controller(game, clients);
            controller.run();
        }
        catch(EvaluatorException e){
            log.error("Initializing the game: {}", e.getMessage());
            return;
        }
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {

        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();

        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(256));

        log.debug("New client connected!");
    }

    private static void handleReceive(SelectionKey key) throws IOException {

        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();

        int petitionType = SocketChannelUtils.receiveInteger(socket);
        log.debug("Petition code {}", petitionType);

        switch (petitionType) {
        case GameType.PETITION_PLAYER_NAME:
            
            String name = SocketChannelUtils.receiveString(socket);
            if(name.length() < 3) {
                buffer.putInt(GameType.ERROR_NAME_TOO_SHORT);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }
            else if(10 < name.length()) {
                buffer.putInt(GameType.ERROR_NAME_TOO_LONG);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }
            else {
                _authPlayers.add( new ClientStructPreGame(name, socket, key) );

                buffer.putInt(GameType.CONFIRMATION_NAME_VALID);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );

                log.debug("Player {} authenticated!", name);
            }
           
            break;
    
        case GameType.PETITION_CREATE_GAME:

            if(_roomPlayers.isEmpty()) {

                ClientStructPreGame client = _authPlayers
                                                    .stream()
                                                    .filter(c -> c.clientSocket == socket)
                                                    .findFirst()
                                                    .orElse(null);
                if(client != null) {
                    _roomPlayers.add( new ClientStructPreGame(client.clientName, socket, key) );
                    _authPlayers.removeIf( c -> c == client );
                    _host = client;

                    buffer.putInt(GameType.CONFIRMATION_CREATED_GAME);
                    key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );

                    log.debug("Player {} is the game host", _host.clientName);
                    log.debug("There is {} players in the game", _roomPlayers.size());
                }
            }
            else {

                buffer.putInt(GameType.ERROR_GAME_ALREADY_CREATED);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }

            break;

        case GameType.PETITION_JOIN_GAME:

            boolean isAlreadyInGame = _roomPlayers.stream().anyMatch( c -> c.clientSocket == socket );  
            if(isAlreadyInGame) {
                // TODO : Desconectar cliente que esta haciendo cosas raras
            }
            else if(_roomPlayers.isEmpty()) {
                buffer.putInt(GameType.ERROR_GAME_NOT_CREATED);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }
            else {
                ClientStructPreGame joinedClient = _authPlayers
                                                        .stream()
                                                        .filter( c -> c.clientSocket == socket )
                                                        .findFirst()
                                                        .orElse(null);
                if(joinedClient != null) {
                    _roomPlayers.add( new ClientStructPreGame(joinedClient.clientName, socket, key) );
                    _authPlayers.removeIf( c -> c == joinedClient );

                    buffer.putInt(GameType.CONFIRMATION_JOINED_GAME);
                    key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );

                    log.debug("There is {} players in the game", _roomPlayers.size());
                }
            }

            break;

        case GameType.PETITION_BOT_LIST:

            if(socket == _host.clientSocket) {
                buffer.putInt(GameType.CONFIRMATION_BOT_LIST);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }

            break;

        case GameType.PETITION_HOST_TRIES_START:

            if(socket == _host.clientSocket) {
                int code = (2 <= _roomPlayers.size()) ? GameType.CONFIRMATION_GAME_STARTS : GameType.CONFIRMATION_GAME_NOT_STARTS;

                buffer.putInt(code);
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }
            break;

        default:
            break;
        }

    }

    private static void handleSend(SelectionKey key) throws IOException {

        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        key.interestOps( key.interestOps() & ~SelectionKey.OP_WRITE );

        int code = buffer.flip().getInt();
        if( isConfirmationCode(code) ) {   // Confirmation type
            handleConfirmation(key, socket, code);
        }
        else if( isErrorCode(code) ) {   // Error type
            handleError(key, socket, code);
        }
    }

    private static boolean isConfirmationCode(final int code) {
        return GameType.CONFIRMATION_TYPE < code && code < GameType.ERROR_TYPE;
    }

    private static boolean isErrorCode(final int code) {
        return GameType.ERROR_TYPE < code;
    }

    private static void handleConfirmation(SelectionKey key, SocketChannel socket, final int code) throws IOException {

        switch (code) {
        case GameType.CONFIRMATION_NAME_VALID:
            SocketChannelUtils.sendInteger(socket, GameType.CONFIRMATION_NAME_VALID);
            break;

        case GameType.CONFIRMATION_CREATED_GAME:
            SocketChannelUtils.sendInteger(socket, GameType.CONFIRMATION_CREATED_GAME);
            break;
        
        case GameType.CONFIRMATION_JOINED_GAME:
            SocketChannelUtils.sendInteger(socket, GameType.CONFIRMATION_JOINED_GAME);
            break;

        case GameType.CONFIRMATION_GAME_STARTS:
            SocketChannelUtils.sendInteger(socket, GameType.CONFIRMATION_GAME_STARTS);
            _gameStarts = true;
            break;

        case GameType.CONFIRMATION_GAME_NOT_STARTS:
            SocketChannelUtils.sendInteger(socket, GameType.CONFIRMATION_GAME_NOT_STARTS);
            break;

        case GameType.CONFIRMATION_BOT_LIST:
            SocketChannelUtils.sendInteger(socket, GameType.CONFIRMATION_BOT_LIST);
            SocketChannelUtils.sendString(socket, "No bots available");
            break;

        default:
            System.out.printf("Unknown confirmation code %d\n", code);
            break;
        }
    }

    private static void handleError(SelectionKey key, SocketChannel socket, final int code) throws IOException {
        
        switch (code) {
        case GameType.ERROR_NAME_TOO_SHORT:
            SocketChannelUtils.sendInteger(socket, GameType.ERROR_NAME_TOO_SHORT);
            break;

        case GameType.ERROR_NAME_TOO_LONG:
            SocketChannelUtils.sendInteger(socket, GameType.ERROR_NAME_TOO_LONG);
            break;

        case GameType.ERROR_GAME_ALREADY_CREATED:
            SocketChannelUtils.sendInteger(socket, GameType.ERROR_GAME_ALREADY_CREATED);
            break;
        
        default:
            System.out.printf("Unknown confirmation code %d\n", code);
            break;
        }
    }

    private static void closeClient(SelectionKey key) {

        try {
            SocketChannel socket = (SocketChannel) key.channel();

            _authPlayers.removeIf( c -> c.clientSocket == socket );
            _roomPlayers.removeIf( c -> c.clientSocket == socket );
            key.cancel();
            socket.close();

            log.debug("Client desconnected!");
        }
        catch(IOException e) {
            log.warn("There was a minor error closing a client that can be ignored: {}", e.getMessage());
        }
    }

}