package com.ucm;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Poker Game
import com.ucm.control.Controller;
import com.ucm.evaluator.Evaluator;
import com.ucm.middleclasses.DTOClient;
import com.ucm.logic.Game;
import com.ucm.SocketUtils;


public class ServerMain {

    private static int MAX_PLAYERS = 9;

    private static ServerSocket _serverSocket;

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * .\mvnw.cmd clean install
     * Run:
     * .\mvnw.cmd -pl server -Prun exec:java
     * Debug:
     * .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * 
     * Run the Tests
     * .\mvnw.cmd test
     */
    public static void main(String[] args) {

        try{

            Evaluator ev = Evaluator.getInstance();
            _serverSocket = new ServerSocket(GameType.PORT);
            System.out.printf("Socket servidor creado en el puerto %d\n", GameType.PORT);

            while(true){
                List<DTOClient> clientList = preGame();
                game(clientList);
            }
        }
        catch(IOException e){
            System.out.printf("ERROR: %s\n", e.getMessage());
        }


        /*
         * try {
         * Evaluator ev = Evaluator.getInstance();
         * Game game = new Game();
         * Controller controller = new Controller(game);
         * controller.run();
         * } catch (IOException e) {
         * System.out.printf("Evaluator class failed on instaciating\n");
         * }
         */
    }

    public static List<DTOClient> preGame(){

        List<DTOClient> _sockets = new ArrayList<>();
        Random rand = new Random();
        int matchID = rand.nextInt();
        int socketCounter = 0;

        try {

            /*
             * 1. Guardar cada conexion en una lista
             * 2. Mientras se pueda aceptar mas jugadores, esperar a un jugador nuevo
             * 3. Si la partida esta llena, esperar al cliente administrar que quiera
             * empezar
             * 4. Si el codigo de empezar coincide con START_GAME
             * -> Hacer llegar el socket a playerList
             * -> controller.run
             * 
             * IMPORTANTE: SOLO PREPARTIDA
             */
 
            while (socketCounter < MAX_PLAYERS) {
                Socket clientSocket = _serverSocket.accept();
                String playerName = SocketUtils.receiveString(clientSocket.getInputStream());
                _sockets.add( new DTOClient(socketCounter, matchID, playerName, clientSocket) );
                socketCounter++;
            }

            Socket adminSocket = _sockets.get(0).socket();
            int code = 0;

            // Avisar a los jugadores si son admin o no
            System.out.printf("Mandando permisos de clientes\n");
            for(int i = 0; i < MAX_PLAYERS; i++){

                Socket currentSocket = _sockets.get(i).socket();
                if(currentSocket == adminSocket)
                    SocketUtils.sendInteger(currentSocket.getOutputStream(), GameType.PLAYER_IS_ADMIN);
                else
                    SocketUtils.sendInteger(currentSocket.getOutputStream(), GameType.PLAYER_NOT_ADMIN);
            }

            // Esperar a que el administrador empiece la partida
            System.out.printf("Esperando al admin para comenzar\n");
            do {
                code = SocketUtils.receiveInt(adminSocket.getInputStream());
            } while (code != GameType.GAME_START_ADMINISTRATOR);

            // Avisar a todos los clientes de que la partida ha comenzado
            System.out.printf("Admin ha comenzado la partida!\n");
            for(int i = 0; i < MAX_PLAYERS; i++){
                SocketUtils.sendInteger(_sockets.get(i).socket().getOutputStream(), GameType.START_GAME);
            }

        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

        return _sockets;
    }

    public static void game(final List<DTOClient> clients) throws IOException{

        Game game = new Game(clients);
        Controller controller = new Controller(game);
        controller.run();
    }

}