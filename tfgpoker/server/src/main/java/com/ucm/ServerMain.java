package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ServerMain {

    public static int MAX_PLAYERS = 2;
    private static ServerSocketChannel serverSocket;
    private static Selector selector;

    private static List<SocketChannel> clientList;
    private static List<ByteBuffer> clientBuffers;

    private static boolean hostWantsToStart;
    private static boolean matchCanStart;

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
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            System.out.printf("Servidor esperando cliente...\n");

            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            clientList = new ArrayList<>();
            clientBuffers = new ArrayList<>();

            // Pregame
            hostWantsToStart = false;
            matchCanStart = false;
            while(!hostWantsToStart || !matchCanStart){

                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        
                    }
                    else if (key.isReadable()) {
                        
                    }
                    else if (key.isWritable()) {
                        
                    }
                }

                // Wait for host client -> Client who created the match
                SocketChannel client = serverSocket.accept();
                if(client != null){
                    clientList.add(client);
                    System.out.printf("New client accepted!\nNumber of players %d\n", clientList.size()); 
                }
                
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
            }
        }
        catch(IOException e) {
            System.out.printf("%s\n", e.getMessage());
        }
        finally {

            if(serverSocket.isOpen()){
                try{
                    serverSocket.close();
                }
                catch(IOException e){
                    System.out.printf("Error closing the server socket: %s\n", e.getMessage());
                }
            }
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

    private static void handleNewClientPetition(SelectionKey key, Selector selector){

    }

}