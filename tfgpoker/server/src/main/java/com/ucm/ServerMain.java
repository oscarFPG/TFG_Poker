package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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

                    if(!key.isValid())
                        continue;


                    if (key.isAcceptable()) {
                        handleAccept(key, selector);
                    }
                    else if (key.isReadable()) {
                        handleReceive(key);
                    }
                    else if (key.isWritable()) {
                        handleSend(key);
                    }
                }
                
            }
        }
        catch(IOException e) {
            System.out.printf("%s\n", e.getMessage());
            e.printStackTrace();
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


    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {

        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, ByteBuffer.allocate(256));
        clientList.add(client);
        System.out.printf("New client connected!\n");
    }

    /**
     * Primer elemento  : 1 byte para tipo de dato: INT(0x1) y STRING(0x2)
     * Segundo elemento : 4 bytes para el tamaño en bytes del dato. INT(4 bytes) y STRING(tantos bytes como caracteres)
     * Tercer elemento  : Tantos bytes como sean necesarios para los datos
     * @param key
     * @throws IOException
     */
    private static void handleReceive(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        try{

            buffer.clear();
            int bytesRead = client.read(buffer);

            if (bytesRead == -1) {
                System.out.println("Cliente desconectado\n");
                client.close();
                return;
            }

            buffer.flip();
            if(buffer.remaining() < 5){
                buffer.clear();
                System.out.printf("Datos incompletos\n");
                client.close(); // Muy restrictivo -> MUY provisional
                return;
            }

            byte type = buffer.get();
            int size = buffer.getInt();
            if(type == GameType.INTEGER_TYPE){
                int value = buffer.getInt();
                System.out.printf("Numero recibido: %d\n", value);
            }
            else if(type == GameType.STRING_TYPE){
                byte value[] = new byte[size];
                buffer.get(value);
                String message = new String(value, StandardCharsets.UTF_8);
                System.out.printf("String recibido: %s\n", message);
            }
            else{
                System.out.printf("Tipo de dato no reconocido\n");
                client.close(); // Muy restrictivo -> MUY provisional
            }

        }
        catch(SocketException e){
            clientList.remove(client);
            key.cancel();
            System.out.printf("Ocurrió algun error con el cliente. Cerrando conexión de forma segura.\n ERROR: %s\n", e.getMessage());
            System.out.printf("Cantidad %d\n", clientList.size());
        }
        catch(IOException e){
            
        }

    }

    private static void handleSend(SelectionKey key) throws IOException {
        
    }

}