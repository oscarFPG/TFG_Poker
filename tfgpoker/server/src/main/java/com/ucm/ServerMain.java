package com.ucm;

// Poker Game
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import com.ucm.control.Controller;
import com.ucm.evaluator.Evaluator;
import com.ucm.logic.Game;
import com.ucm.SocketUtils;


public class ServerMain {

    private static int MAX_PLAYERS = 3;

    private static ServerSocketChannel _serverSocket;
    private static ByteBuffer _buffer;

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

            _serverSocket = ServerSocketChannel.open();
            _serverSocket.bind( new InetSocketAddress("localhost", GameType.PORT) );
            System.out.printf("Socket servidor creado en el puerto %d\n", GameType.PORT);

            SocketChannel client = _serverSocket.accept();
            System.out.print("Nuevo cliente aceptado!\n");

            while(true){
                preGame();
                //game();
            }
        }
        catch(IOException e){
            System.out.printf("Error creating ServerSocketChannel: %s\n", e.getMessage());
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

        SocketChannel socketList[] = new SocketChannel[3];
        int socketCounter = 0;

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
                socketCounter++;
            }

            SocketChannel adminSocket = socketList[0];
            int code = 0;

            // Avisar a los jugadores si son admin o no
            System.out.printf("Mandando permisos de clientes\n");
            for(int i = 0; i < MAX_PLAYERS; i++){
                if(socketList[i] == adminSocket){}
                    //SocketUtils.sendInteger(socketList[i].getOutputStream(), GameType.PLAYER_IS_ADMIN);
                //else
                    //SocketUtils.sendInteger(socketList[i].getOutputStream(), GameType.PLAYER_NOT_ADMIN);
            }

            // Esperar a que el administrador empiece la partida
            System.out.printf("Esperando al admin para comenzar\n");
            do {
                //code = SocketUtils.receiveInt(adminSocket.getInputStream());
            } while (code != GameType.GAME_START_ADMINISTRATOR);
            System.out.printf("Admin ha comenzado la partida!\n");

            // Avisar a todos los clientes de que la partida ha comenzado
            for(int i = 0; i < MAX_PLAYERS; i++){
                //SocketUtils.sendInteger(socketList[i].getOutputStream(), GameType.START_GAME);
            }

            // Empezar partida
            // ...

        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }
    }

}