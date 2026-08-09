package com.ucm.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStruct;
import com.ucm.common.ClientStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.server.control.Controller;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.logic.Game;
import com.ucm.server.players.Spectator;


public class ServerTCP {

    private static final Logger log = LogManager.getLogger(ServerTCP.class);

    private String _serverIP;
    private ServerSocket _serverSocket;
    private ExecutorService _executor;
    private AtomicInteger _idGenerator;

    private List<ClientThread> _roomPlayers;
    private List<BotStruct> _roomBots;
    private Spectator _spectator;
    private GameConfig _gameConfig;


    public ServerTCP(final int port) throws IOException, InterruptedException {
        _serverIP = showServerIP();
        _serverSocket = new ServerSocket(port);
        _executor = Executors.newFixedThreadPool(GameType.MAX_PLAYERS + 1); // Full poker game(9 players) + 1 spectator
        _idGenerator = new AtomicInteger(0);

        _roomPlayers = Collections.synchronizedList( new ArrayList<>() );
        _roomBots = Collections.synchronizedList( new ArrayList<>() );
        _spectator = new Spectator(null);
        _gameConfig = new GameConfig();

        log.debug("Server public IP: {}", _serverIP);
        log.debug("Server started on port {}", port);
    }


    public void startPregame() {

        while(!_serverSocket.isClosed()) {

            try {

                Socket socket = _serverSocket.accept();
                ClientThread connectedClient = new ClientThread(
                    socket, _serverSocket, _idGenerator,    // Server properties
                    _roomPlayers, _roomBots, _spectator,    // Player list, bot list and spectator(optional)
                    _gameConfig     // Game configuration
                );
               _executor.execute( connectedClient );

                log.debug("New client connected!");
            }
            catch(IOException e) {
                log.error("Error accepting client connection: {}", e.getMessage());
            }
        }
        log.debug("Terminating pregame phase...");
    }

    public void startGame(final List<ClientStruct> players, final List<BotStruct> bots, final Spectator spectator, final GameConfig config) {

        log.debug("--- Poker game ---");

        try {
            Game game = new Game(players, bots, spectator, config);
            Controller controller = new Controller(game);
            controller.run();
        }
        catch(EvaluatorException e) {
            log.error("{}", e.getMessage());
        }
        catch(CancelGameException e) {

            log.debug("Game cancelled by server: {}", e.getMessage());
            for(ClientStruct cs : players) {

                try {
                    SocketUtils.sendInteger(cs.socket().getOutputStream(), GameType.ERROR_GAME_CANCELS);
                }
                catch(IOException ex) {
                    log.warn("Minor error trying to notify player {} about game cancellation: {}", cs.name(), ex.getMessage());
                }
            }
        
        }
        finally {
            cleanUp(players);
        }

    }


    public List<ClientStruct> getRoomPlayers() {

        List<ClientStruct> players = new ArrayList<>();
        for(ClientThread ct : _roomPlayers) {
            
            if( ct.getIsHost() ) {
                players.add( ClientStruct.createHostPlayer(ct.getPlayerName(), ct.getPlayerSocket()) );
            }
            else {
                players.add( ClientStruct.createGuestPlayer(ct.getPlayerName(), ct.getPlayerSocket()) );
            }
        }

        return players;
    }

    public List<BotStruct> getRoomBots () {
        return new ArrayList<>(_roomBots);
    }

    public Spectator getSpectator() {
        return _spectator;
    }

    public GameConfig getGameConfigDeepCopy() {
        return new GameConfig(_gameConfig);
    }

    
    private String showServerIP() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                                .newBuilder()
                                .uri(URI.create("https://api.ipify.org"))
                                .GET()
                                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String serverIP = response.body();
        
        return serverIP;
    }

    private void cleanUp(final List<ClientStruct> players) {

        log.debug("Cleaning up server resources...");
        for(ClientStruct cs : players) {
            try {
                if (cs.socket().isConnected() || !cs.socket().isClosed()) {
                    cs.socket().close();
                    log.debug("Socket of player {} closed!", cs.name());
                }
            }
            catch (IOException e) {
                log.warn("Minor error trying to close connection with player {} : {}", cs.name(), e.getMessage());
            }
        }

        log.debug("Closing game history...");
        PokerHistory.endMatch();

        log.debug("Closing server socket...");
        try {
            if (!_serverSocket.isClosed()) {
                _serverSocket.close();
            }
        }
        catch (IOException e) {
            log.warn("Minor error trying to close server socket: {}", e.getMessage());
        }

        log.debug("Executor service shutdown!");
        _executor.shutdownNow();
    }

}