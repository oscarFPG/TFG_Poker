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

    private static int MAX_PLAYERS = 3;
    private static int port = 5005;
    private static int idCounter = 0;


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
            _serverSocket = new ServerSocket(port);
            System.out.printf("Socket servidor creado en el puerto %d\n", port);

            while(true){
                preGame();
                //game();
            }
        }
        catch(IOException e){
            
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

    public static void preGame(){

        Socket socketList[] = new Socket[3];
        int socketCounter = 0;

        Random rand = new Random();
        int matchID = rand.nextInt();

        List<DTOClient> _sockets = new ArrayList<>();

        try {

            /*
             * 1. Guardar cada conexion en una lista
             * 2. Mientras se pueda aceptar más jugadores, esperar a un jugador nuevo
             * 3. Si la partida está llena, esperar al cliente administrar que quiera
             * empezar
             * 4. Si el código de empezar coincide con START_GAME
             * -> Hacer llegar el socket a playerList
             * -> controller.run
             * 
             * IMPORTANTE: SOLO PREPARTIDA
             */


            
            while (socketCounter < MAX_PLAYERS) {
                socketList[socketCounter] = _serverSocket.accept();
                String playerName = SocketUtils.receiveString(socketList[socketCounter].getInputStream());
                _sockets.add(new DTOClient(socketCounter, matchID, playerName, socketList[socketCounter]));
                socketCounter++;
            }

            Socket adminSocket = socketList[0];
            int code = 0;

            // Avisar a los jugadores si son admin o no
            System.out.printf("Mandando permisos de clientes\n");
            for(int i = 0; i < MAX_PLAYERS; i++){
                if(socketList[i] == adminSocket)
                    SocketUtils.sendInteger(socketList[i].getOutputStream(), GameType.PLAYER_IS_ADMIN);
                else
                    SocketUtils.sendInteger(socketList[i].getOutputStream(), GameType.PLAYER_NOT_ADMIN);
            }

            // Esperar a que el administrador empiece la partida
            System.out.printf("Esperando al admin para comenzar\n");
            do {
                code = SocketUtils.receiveInt(adminSocket.getInputStream());
            } while (code != GameType.GAME_START_ADMINISTRATOR);
            System.out.printf("Admin ha comenzado la partida!\n");

            // Avisar a todos los clientes de que la partida ha comenzado
            for(int i = 0; i < MAX_PLAYERS; i++){
                SocketUtils.sendInteger(socketList[i].getOutputStream(), GameType.START_GAME);
            }
            //
            // Empezar partida
            // ...
            
            

        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }
    }

    public static void game(final List<DTOClient> sockets) throws IOException{

        Game g = new Game(sockets);
        Controller cl = new Controller(g);
        cl.run();
    }

}