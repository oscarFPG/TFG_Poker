package com.ucm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.control.Controller;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;
import com.ucm.server.middleclasses.ClientStruct;


public class ServerMain {
    

    private static final Logger log = LogManager.getLogger(ServerMain.class);
    
    public static int MAX_PLAYERS = 9;

    private static class ClientHandler extends Thread {

        private int id;
        private Socket socket;

        public ClientHandler(Socket s, int ID) {
            id = ID;
            this.socket = s;
        }

        public void run() {

            for(int i = 0; i < 4; i++) {
                try {
                    System.out.printf("Mostrando dato desde Thread %d\n", id);
                    Thread.sleep(1000);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
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

            List<ClientHandler> players = preGame();
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


    private static List<ClientHandler> preGame() throws IOException {

        ServerSocket serverSocket = new ServerSocket(GameType.PORT);
        List<ClientHandler> clients = new ArrayList<>();

        int ids = 0;
        while(clients.size() < MAX_PLAYERS) {

            Socket socket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(socket, ids);

            synchronized (clients) {
                clients.add(handler);
            }
            handler.start();

            ++ids;
        }

        return clients;
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