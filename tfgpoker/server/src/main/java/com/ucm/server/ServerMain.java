package com.ucm.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.server.control.Controller;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;
import com.ucm.server.middleclasses.ClientStruct;

import opennlp.tools.stemmer.snowball.indonesianStemmer;


public class ServerMain {
    

    private static final Logger log = LogManager.getLogger(ServerMain.class);
    
    public static int MAX_PLAYERS = 9;

    private static List< ClientStruct<Integer> > _clients = Collections.synchronizedList(new ArrayList<>());

    private static boolean _gameStarts = false;


    private static class ClientThread extends Thread {

        private String clientName;
        private Socket socket;
        private BlockingQueue<Integer> queue;

        public ClientThread(Socket s, BlockingQueue<Integer> q) {
            socket = s;
            queue = q;
        }

        public void run() {

            try {

                String name;
                int code;
                while(!_gameStarts) {

                    name = SocketUtils.receiveString(socket.getInputStream());
                    clientName = name;
                    log.debug("Client name is {}", clientName);

                    code = SocketUtils.receiveInt(socket.getInputStream());
                    if(code == GameType.PETITION_CREATE_GAME) {
                        createGame();
                    }
                    else if(code == GameType.PETITION_JOIN_GAME) {
                        joinGame();
                    }
                    else {
                        throw new IOException( String.format("Unknown code %d", code) );
                    }

                    log.debug("End of connection for client {}", clientName);
                    return;

                }
            }
            catch(IOException e) {
                log.error("There was an error with a client: {}", e.getMessage());
            }
            catch (InterruptedException e) {
                log.error("Thread interrupted: {}", e.getMessage());
            }
            
        }

        private void createGame() throws IOException, InterruptedException {

            log.debug("Client {} wants to create a game", clientName);
            SocketUtils.sendInteger(socket.getOutputStream(), GameType.CONFIRMATION_CREATE_GAME);

            int bots = SocketUtils.receiveInt(socket.getInputStream());
            if(bots == GameType.PETITION_ADD_BOTS) {
                log.debug("Client {} wants to add bots", clientName);
                log.debug("Loop in where the client must configure the bots");
                waitSeconds(3);
            }
            else if(bots == GameType.PETITION_NOT_ADD_BOTS) {
                log.debug("Client {} does not want to add bots", clientName);
            }

           _clients.add( new ClientStruct<>(clientName, socket, queue) );
            Thread sendNewPlayerInfoThread = new Thread(() -> {

                boolean exit = false;
                while(!exit) {

                    try {
                        
                        System.out.printf("Waiting...\n");
                        int code = queue.take();
                        if(code == GameType.EVENT_PLAYER_JOINED) {
                            sendListInfo(socket);
                        }

                    }
                    catch (IOException | InterruptedException e) {
                        log.debug("There was an error : {}", e.getMessage());
                    }


                }

            });
            sendNewPlayerInfoThread.start();
            sendNewPlayerInfoThread.join();
        }

        private void joinGame() throws IOException, InterruptedException {

            log.debug("Client {} wants to join to a game", clientName);
            if(_clients.isEmpty()) {
                log.debug("Notify client that it cannot join if there is no games");
                socket.close();
                return;
            }

            _clients.add( new ClientStruct<>(clientName, socket, queue) );
            
            for(ClientStruct<Integer> cl : _clients)
                cl.queue().put(GameType.EVENT_PLAYER_JOINED);

            while(true) {

                int code = queue.take();
                if(code == GameType.EVENT_PLAYER_JOINED) {
                    sendListInfo(socket);
                }
            }

        }

        private void sendListInfo(Socket socket) throws IOException {

            List< ClientStruct<Integer> > copy = new ArrayList<>(_clients);

            SocketUtils.sendInteger(socket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
            SocketUtils.sendInteger(socket.getOutputStream(), copy.size());
            for(ClientStruct<Integer> cl : copy) {
                SocketUtils.sendString(socket.getOutputStream(), cl.name());
            }
        }

        private void waitSeconds(final int sec) {

            try {

                for(int times = sec; 0 < times; times--) {
                    log.debug("Ready in {}", times);
                    Thread.sleep(1000);
                }
            }
            catch(InterruptedException e) {
                log.error("{}", e.getMessage());
            }
        }

    }

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
    public static void main(String[] args) {

        try {

            showServerIP();

            preGame();
            //game(players);
        }
        catch(IOException | InterruptedException e) {
            log.fatal("Couldnt get the public server ip {}", e.getMessage());
            return;
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

        ServerSocket serverSocket = new ServerSocket(GameType.PORT);
        while(!_gameStarts) {

            Socket socket = serverSocket.accept();
            log.debug("New client connected!");

            if(_clients.size() < MAX_PLAYERS) {
                ClientThread client = new ClientThread(socket, new LinkedBlockingQueue<>());
                client.start();
            }
            else {

                socket.close();
                log.debug("Denying more connections, server full");
            }
        }
    }

    private static void game(List<ClientStruct> clients) {

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

}