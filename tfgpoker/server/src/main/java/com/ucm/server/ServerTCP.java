package com.ucm.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameConfig;
import com.ucm.common.GameInfo;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;
import com.ucm.server.control.Controller;

import java.net.http.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


public class ServerTCP {

    

    private static final Logger log = LogManager.getLogger(ServerTCP.class);

    private String _serverIP;
    private int _serverPort;
    private ServerSocket _serverSocket;
    private ExecutorService _executor;

    private List<ClientThread> _roomPlayers;
    private GameConfig _gameConfig;


    public ServerTCP(final int port) throws IOException, InterruptedException {
        _serverPort = port;
        _serverSocket = new ServerSocket(port);
        _executor = Executors.newFixedThreadPool(GameType.MAX_PLAYERS);
        _roomPlayers = Collections.synchronizedList( new ArrayList<>() );
        _gameConfig = new GameConfig();

        log.debug("Server started on port {}", port);
        _serverIP = showServerIP();
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
        log.debug("Server public IP: {}", serverIP);

        return serverIP;
    }

    public List<ClientThread> startPregame() {

        while(!_serverSocket.isClosed()) {

            try {

                Socket socket = _serverSocket.accept();
               _executor.execute( new ClientThread(socket, _roomPlayers, _serverSocket, _gameConfig) );
            
                log.debug("New client connected!");
            }
            catch(IOException e) {
                log.error("Error accepting client connection: {}", e.getMessage());
            }
        }
        log.debug("Terminating pregame phase...");

        return _roomPlayers;
    }

    public GameConfig getGameConfigDeepCopy() {
        return new GameConfig(_gameConfig);
    }

    public void startGame(final GameInfo info) {

        log.debug("--- Poker game ---");

        try {
            Game game = new Game();
            Controller controller = new Controller(game, info.players);
            controller.run();
        }
        catch(EvaluatorException e) {
            log.error("{}", e.getMessage());
        }
        

    }

}