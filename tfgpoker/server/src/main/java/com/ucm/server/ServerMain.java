package com.ucm.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


// Poker Game
import com.ucm.server.middleclasses.ClientStructGame;
import com.ucm.server.players.ChatgptLLM;
import com.ucm.common.*;
import com.ucm.server.control.Controller;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;

// LLMs
import dev.langchain4j.model.googleai.*;


public class ServerMain {

    private static final Logger log = LogManager.getLogger(ServerMain.class);

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


    public static int MAX_PLAYERS = 9;
    private static SocketChannel _host;
    private static boolean _hostWantsToStart;
    
    private static List<String> _botList;

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * Run server:
     *      .\mvnw.cmd -pl server -Prun exec:java
     * Run server in local mode (no server, only for testing):
     *      .\mvnw.cmd -pl server -Prun-local exec:java -Dn=<int>
     * Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * Debug server in local mode (no server, only for testing):
     *      .\mvnwDebug.cmd -pl server -Pdebug-local exec:java -Dn=<int>
     * Run the tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) throws IOException {


        ChatgptLLM chatgpt = new ChatgptLLM(0, 1000);
        String result = chatgpt.actionMakePlay(0, 0, 0);
        System.out.printf(result);
        
        boolean no = false;
        while(!no) {

        }



        // Local mode for testing without real clients connected by sockets
        if(args.length > 0 && args[0].equalsIgnoreCase("local")){
            runGameInModeLocal(args);
            return;
        }


        try {

            log.debug("Running in server mode...");
            String serverIP = getServerPublicIP();
            List<ClientStructPreGame> joinedClients = preGame();
            List<ClientStructGame> players = new ArrayList<>();
            for(ClientStructPreGame cs : joinedClients){
                cs.clientSocket.configureBlocking(true);
                players.add( new ClientStructGame(cs.clientName, cs.clientSocket.socket()) );
            }
            game(players);


        }
        catch(IOException e) {
            log.fatal("{}", e.getMessage());
        }
        catch(InterruptedException e) {
            log.fatal("{}", e.getMessage());
        }
    }

    private static void runGameInModeLocal(String[] args) {

        int n_players = 2;
        if(args.length > 1){
            try{
                n_players = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException e){
                log.error("Invalid number of players!Using default value: {}", n_players);
            }
        }

        log.debug("Running in local mode...");
        log.debug("Number of players: {}", n_players);

        try{
            Game game = new Game();
            Controller controller = new Controller(game, n_players);
            controller.run();

            log.debug("Server finished!");
        }
        catch(EvaluatorException e){
            log.error("Initializing the game: {}", e.getMessage());
        }
    }


    private static String getServerPublicIP() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                                .newBuilder()
                                .uri(URI.create("https://api.ipify.org"))
                                .GET()
                                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String serverIP = response.body();
        log.debug("Server public IP obtained: {}", serverIP);

        return serverIP;
    } 

    private static List<ClientStructPreGame> preGame() {

        List<ClientStructPreGame> roomList = new ArrayList<>();     // Player list that enter the game
        List<ClientStructPreGame> clientList = new ArrayList<>();   // Client list that tries to play
        _botList = new ArrayList<>();

        ServerSocketChannel serverSocket = null;
        Selector selector = null;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            log.debug("Server up, waiting for clients...");

            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            _hostWantsToStart = false;
            while (!_hostWantsToStart) {

                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid())
                        continue;


                    if (key.isAcceptable()) {
                        handleAccept(key, selector);
                    } 
                    else if (key.isReadable()) {
                        handleReceive(key, clientList, roomList);
                    } 
                    else if (key.isWritable()) {
                        handleSend(key);
                    }
                }
            }
            log.debug("Host empieza la partida!");

            // Cancel all clients left keys -> Important!
            for (SelectionKey key : selector.keys()) {
                key.cancel();
            }
            selector.selectNow();

            // Send to all clients the GAME_STARTS flag
            ByteBuffer broadcastBuffer = ByteBuffer.allocate(Integer.BYTES);
            broadcastBuffer.putInt(GameType.GAME_STARTS);
            for(ClientStructPreGame cs : roomList){
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
            for(ClientStructPreGame cs : clientList){
                try {
                    cs.key.cancel();
                    cs.clientSocket.close();
                }
                catch(IOException ignored){}
            } 
        } 
        catch (IOException e) {
            log.error("Something happend with clients socket {}", e.getMessage());
        } 
        finally {

            if (serverSocket.isOpen()) {
                try {
                    serverSocket.close();
                    selector.close();
                }
                catch (IOException e) {
                    log.error("Closing the server socket: {}", e.getMessage());
                }
            }
        }

        return roomList;
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

        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(128));
        log.debug("Nuevo cliente conectado!");
    }

    private static void handleReceive(SelectionKey key, List<ClientStructPreGame> clientList, List<ClientStructPreGame> roomList) {

        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead;
        byte tipo;

        try {

            bytesRead = socket.read(buffer);
            if (bytesRead == -1) {
                key.cancel();
                socket.close();
                log.debug("Connection closed!");
                return;
            }

            buffer.flip();
            tipo = buffer.get();
            log.debug("Tipo de peticion {}", tipo);

            switch (tipo) {
            case GameType.DATA_TYPE_NAME:
                int msgSize;
                byte[] msgBytes;

                msgSize = buffer.getInt();
                msgBytes = new byte[msgSize];
                buffer.get(msgBytes);
                String clientName = new String(msgBytes, StandardCharsets.UTF_8);
                log.debug("Client {} authenticated!", clientName);

                // Almacenar nombre para relacionar socket-nombre si esta en la lista de clientes
                // Esta lista es distinta a la lista de jugadores que SI que van a entrar a partida
                log.debug("Cliente {} almacenado en la lista de clientes!", clientName);
                clientList.add( new ClientStructPreGame(clientName, socket, key) );

                break;

            case GameType.DATA_TYPE_PETITION:
                int code = buffer.getInt();
                log.debug("Petition type received {}", code);
                handleClientPetition(key, code, clientList, roomList);

                break;

            default:
                log.debug("Tipo desconocido: {}", tipo);
            }
        }
        catch(IOException e){
            log.error("Something strange ocurred with the clients socket: {}", e.getMessage());
			try {
				key.cancel();
				socket.close();
			}
			catch (IOException ignored) {}
        }

        buffer.clear();
    }

    private static void handleSend(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

    }

    // Game options
    private static void handleClientPetition(SelectionKey key, final int petition, List<ClientStructPreGame> clientList, List<ClientStructPreGame> roomList) {

        SocketChannel client = (SocketChannel) key.channel();
        switch (petition) {
        case GameType.CREATE_PETITION:

            log.debug("Peticion CREATE del cliente");
            if(roomList.isEmpty()){

                log.debug("Creando partida!");
                ClientStructPreGame hostClient = clientList.stream()
                    .filter(c -> c.clientSocket == client).findFirst().
                    orElse(null);

                if(hostClient != null){
                    clientList.remove(hostClient);
                    roomList.add(hostClient);
                    _host = client;  // Select the host to wait for his petition to start the game
                    log.debug("Client {} selected as host", hostClient.clientName);
                }
            }
            else{

                log.debug("Partida ya existente!");
                try {
                    client.close();
                }
                catch (IOException e) {
                    log.error("Closing connection with client {}", e.getMessage());
                }
            }
            break;

        case GameType.JOIN_PETITION:

            log.debug("Peticion JOIN del cliente");
            if(!roomList.isEmpty() && roomList.size() < MAX_PLAYERS){     // La partida ha sido creada y ha, al menos, dos jugadores

                ClientStructPreGame joiningClient = clientList.stream()
                    .filter(c -> c.clientSocket == client).findFirst().
                    orElse(null);

                if(joiningClient != null){
                    roomList.add( joiningClient );
                    clientList.removeIf(c -> c.clientSocket == joiningClient.clientSocket);
                    log.debug("Uniendo jugador a la partida! Jugadores actuales: {}", roomList.size());
                }
            }
            else{

                log.debug("Partida no creada o llena!");
                try {
                    client.close();
                    key.cancel();
                }
                catch(IOException e) {
                    log.error("Trying to close connection safely: {}", e.getMessage());
                }
            }
            break;
    
        case GameType.HOST_START_GAME_PETITION:

            // Other client except the true host tries to start the game
            if(client != _host){

                log.debug("Only the host can start the game!");
                try{
                    ClientStructPreGame cs = roomList.stream()
                        .filter(c -> c.clientSocket == client)
                        .findFirst()
                        .orElse(null);

                    clientList.remove(cs);
                    roomList.remove(cs);
                    key.cancel();
                    client.close();
                }
                catch (IOException ignored){}

                return;
            }

            if(roomList.size() >= 2){
                log.debug("Host starts the game succesfully with {} players!", roomList.size());
                _hostWantsToStart = true;
            }
            else {
                log.debug("Cannot start the game with less than 2 players!");
                // TODO : Avisar al cliente de esto
            }
            
            break;

        case GameType.ADD_BOT_PETITION:

            if(client != _host){

                log.debug("Only the host can add bots!");
                try {
                    ClientStructPreGame cs = roomList.stream()
                        .filter(c -> c.clientSocket == client)
                        .findFirst()
                        .orElse(null);

                    clientList.remove(cs);
                    roomList.remove(cs);
                    key.cancel();
                    client.close();
                }
                catch (IOException ignored){}

                return;
            }

            log.debug("Host wants to add a bot");


            break;
            
        default:
            log.error("Unknown petition {}", petition);
            break;
        }
        
    }

}