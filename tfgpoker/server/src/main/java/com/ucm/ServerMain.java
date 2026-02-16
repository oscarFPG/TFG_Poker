package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class ServerMain {

    public static int MAX_PLAYERS = 9;

    private static Selector selector;
    private static ServerSocketChannel serverSocket;

    private static boolean hostWantsToStart;
    
    private class ClientStruct {

        public String clientName;
        public SocketChannel clientSocket;

        ClientStruct(final String name, final SocketChannel s){
            clientName = name;
            clientSocket = s;
        }
    }
    
    
    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * .\mvnw.cmd clean install
     * Run:
     * .\mvnw.cmd -pl server -Prun exec:java
     * Debug:
     * .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * Run the Tests
     * .\mvnw.cmd test
     */
    public static void main(String[] args) {

        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            System.out.printf("Server up, waiting for clients...\n");

            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            hostWantsToStart = false;
            while (!hostWantsToStart) {

                // Accept, receive or send data to current clients
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid())
                        continue;


                    if (key.isAcceptable()) {       // Client connects
                        handleAccept(key, selector);
                    } 
                    else if (key.isReadable()) {    // Clients send data to server
                        handleReceive(key);
                    } 
                    else if (key.isWritable()) {    // Send data to clients
                        handleSend(key);
                    }
                }

            }
        } 
        catch (IOException e) {
            System.out.printf("%s\n", e.getMessage());
        } 
        finally {

            if (serverSocket.isOpen()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.printf("Error closing the server socket: %s\n", e.getMessage());
                }
            }
        }

    }


    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {

        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(256));
        System.out.printf("Nuevo cliente conectado!\n");
    }

    private static void handleReceive(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();


        /*
        
        switch por tipo de accion:
            - Mandar nombre
            - Mandar peticion(CREATE, JOIN, etc...)
        */

    }

    private static void handleSend(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

    }

    private static void handleClientPetition(SelectionKey key, final int petition) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

    }

}