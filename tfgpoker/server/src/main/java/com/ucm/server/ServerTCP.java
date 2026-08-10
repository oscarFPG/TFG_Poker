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

    /**
     * Server public IP address
     */
    private String _serverIP;

    /**
     * Socket used by the server to accept new client connections
     */
    private ServerSocket _serverSocket;

    /**
     * Service that admits up to 9 players simultaneously
     */
    private ExecutorService _executor;

    /**
     * ID generator for in-game player IDs
     */
    private AtomicInteger _idGenerator;


    /**
     * List of human clients in the room game list
     * @see ClientThread
     */
    private List<ClientThread> _roomPlayers;

    /**
     * List of bots in the room game list
     * @see BotStruct
     */
    private List<BotStruct> _roomBots;

    /**
     * Human spectator. It does not play but it can watch the game in real-time
     * @see Spectator
     */
    private Spectator _spectator;

    /**
     * Current game configuration
     * @see GameConfig
     */
    private GameConfig _gameConfig;


    /**
     * Full server constructor
     * @param port used by the server. Ex.: 8000
     * @throws IOException caused by some error with the server socket
     * @throws InterruptedException caused by the server trying to see his public IP address
     */
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


    /**
     * Pregame of poker game.
     * Users can join or left the waiting room and wait until the game starts.
     * This methods return only if there is some fatal error or the server socket is closed.
     * This server socket can be closed by the host to start the game
     */
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

    /**
     * Poker game method. This methods only return if the game finishes or there is some fatal error.
     * In any case, all the connections are closed
     * @param players connected and ready to play.
     * @param bots selected to play.
     * @param spectator in the game, if selected
     * @param config Game configuration for the game
     */
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


    /**
     * Get the human players list
     * @return list of players
     */
    public List<ClientStruct> getRoomPlayers() {

        List<ClientStruct> players = new ArrayList<>();
        for(ClientThread ct : _roomPlayers) {
            
            if( ct.getIsHost() ) {
                players.add( ClientStruct.createHostPlayer(ct.getPlayerID(), ct.getPlayerName(), ct.getPlayerSocket()) );
            }
            else {
                players.add( ClientStruct.createGuestPlayer(ct.getPlayerID(), ct.getPlayerName(), ct.getPlayerSocket()) );
            }
        }

        return players;
    }

    /**
     * Get the bot list
     * @return list of bots
     */
    public List<BotStruct> getRoomBots () {
        return new ArrayList<>(_roomBots);
    }

    /**
     * Get the spectator. Can be null.
     * @return spectator reference
     */
    public Spectator getSpectator() {
        return _spectator;
    }

    /**
     * Generates a deep copy of the current game configuration
     * @return a new @link{GameConfig} object
     */
    public GameConfig getGameConfigDeepCopy() {
        return new GameConfig(_gameConfig);
    }

    /**
     * Checks and returns the public IP address of this machine executing the server program
     * @return an @link{String} of the server public IP address
     * @throws IOException
     * @throws InterruptedException
     */
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

    /**
     * Disconnects all the human player sockets in the game room and closes the server socket 
     * @param players joined to the game
     */
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