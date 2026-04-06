package com.ucm.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;

import java.net.http.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;



public class ServerTCP {


    private static final Logger log = LogManager.getLogger(ServerTCP.class);

    private String _serverIP;
    private int _serverPort;
    private ServerSocket _serverSocket;
    private ExecutorService _executor;

    private AtomicInteger _connectionsCounter;


    public ServerTCP(final int port) throws IOException, InterruptedException {
        _serverPort = port;
        _serverSocket = new ServerSocket(port);
        _executor = Executors.newFixedThreadPool(GameType.MAX_PLAYERS);
        _connectionsCounter = new AtomicInteger(0);

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

    public void start() {

        while(true) {

            try {

                Socket socket = _serverSocket.accept();

               _executor.execute( new ClientThread(socket) );
                _connectionsCounter.incrementAndGet();
            
                log.debug("New client connected!");
            }
            catch(IOException e) {
                log.error("Error accepting client connection: {}", e.getMessage());
            }
        }

    }

}