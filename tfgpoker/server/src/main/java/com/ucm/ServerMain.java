package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;


public class ServerMain {

    public static int MAX_PLAYERS = 9;

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

        ServerSocketChannel serverSocket;
        List<SocketChannel> clientList = new ArrayList<>();
        Thread clientThreads[] = new Thread[MAX_PLAYERS];
        try{
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            System.out.printf("Servidor esperando cliente...\n");

            // Wait for host client -> Client who created the match
            SocketChannel hostClient = null;
            while( hostClient == null ){
               hostClient = serverSocket.accept();
            }
            System.out.printf("Cliente conectado!\n");
            clientList.add(hostClient);

            // Wait for up to 8 more clients
            clientThreads[0] = new Thread(() -> {
                System.out.printf("Host\n");
            });

            clientThreads[0].start();
            for(int i = 1; i < MAX_PLAYERS; i++){

                final int id = i;
                clientThreads[i] = new Thread( () -> {
                        
                    SocketChannel newClient = waitPlayer(id);
                    if(newClient != null){
                        clientList.add(newClient);
                    }

                });
                clientThreads[i].start();
            }

        }
        catch(IOException exception){
            System.out.printf("ERROR: %s\n", exception.getMessage());
        }

    }

    private static SocketChannel waitPlayer(final int threadID){

        System.out.printf("Esperando cliente desde el thread con id %d...\n", threadID);

        return null;
    }

    /* 
    public static void preGame(){

        Socket socketList[] = new Socket[3];
        int socketCounter = 0;

        try {

            // 1. Guardar cada conexion en una lista
            // 2. Mientras se pueda aceptar más jugadores, esperar a un jugador nuevo
            // 3. Si la partida está llena, esperar al cliente administrar que quiera empezar
            // 4. Si el código de empezar coincide con START_GAME

            while (socketCounter < MAX_PLAYERS) {
                socketList[socketCounter] = _serverSocket.accept();
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

            // Empezar partida
            // ...

        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }
    }
    */

}