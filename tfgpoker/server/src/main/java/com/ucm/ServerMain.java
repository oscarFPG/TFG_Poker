package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;


public class ServerMain {

    public static int MAX_PLAYERS = 2;
    private static ServerSocketChannel serverSocket;
    private static boolean hostWantsToStart;
    private static boolean matchStarts;

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

        List<SocketChannel> clientList = new ArrayList<>();
        List<ByteBuffer> clientBuffers;
        try{
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            System.out.printf("Servidor esperando cliente...\n");

            // Wait for host client -> Client who created the match
            SocketChannel client = null;
            while( client == null ){
               client = serverSocket.accept();
            }
            clientList.add(client);
            hostWantsToStart = false;
            matchStarts = false;
            System.out.printf("New client accepted!\nNumber of players %d\n", clientList.size()); 
            
            // Receive client petition
            int bytesRead = 0;
            ByteBuffer buffer = ByteBuffer.allocate( Integer.BYTES );
            System.out.printf("Server waiting for clients petition\n");
            while(bytesRead == 0){
                bytesRead = client.read(buffer);
            }
            buffer.flip();

            // Answer the clients petition
            int value = buffer.getInt();
            if(value == GameType.CREATE_PETITION){
                System.out.printf("Client wants to create a match\n");
            }
            else if(value == GameType.JOIN_PETITION){
                System.out.printf("Client wants to join to a match\n");
            }

            /*
            // Wait up to 9 players more
            while( clientList.size() != MAX_PLAYERS ){

                SocketChannel client = serverSocket.accept();
                if(client != null){
                    clientList.add(client);
                    System.out.printf("New client accepted!\nNumber of players %d\n", clientList.size());
                }
            }

            // Create a ByteBuffer to receive/send from/to any client 
            clientBuffers = new ArrayList<>( clientList.size() );
            for(int i = 0; i < clientList.size(); i++){

                // Escribir dato en el buffer del cliente
                clientBuffers.add( ByteBuffer.allocate(256) );
                ByteBuffer buffer = clientBuffers.get(i);
                buffer = ByteBuffer.allocate( Integer.BYTES );
                buffer.putInt( GameType.GAME_STARTS );
                buffer.flip();

                // Enviar dato al cliente
                SocketChannel client = clientList.get(i);
                while(buffer.hasRemaining()){
                    client.write(buffer);
                }
                System.out.printf("Valor %d enviado al cliente %d!\n", GameType.GAME_STARTS, i);
            }
            */

        }
        catch(IOException exception){
            System.out.printf("ERROR: %s\n", exception.getMessage());
        }

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