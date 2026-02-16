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
import java.lang.Thread;

public class ServerMain {

    public static int MAX_PLAYERS = 2;
    private static Selector selector;
    private static ServerSocketChannel serverSocket;
    private static ServerSocketChannel allowPlayersServerSocket;
    private static List<SocketChannel> clientList;
    private static HostPrivacyThread hostPrivacy;
    private static Thread allowPlayersThread;

    private static boolean hostWantsToStart;
    private static boolean matchCanStart;
    private static boolean hostAllowsMorePlayers;

    public static class HostPrivacyThread implements Runnable {

        public static ServerSocketChannel server;
        public static SocketChannel host;
        public static ByteBuffer hostBuffer;

        HostPrivacyThread(ServerSocketChannel s) {
            server = s;
            hostBuffer = ByteBuffer.allocate(Integer.BYTES);
        }

        @Override
        public void run() {

            // Connect
            try {

                System.out.printf("Waiting host...\n");
                while (host == null)
                    host = server.accept();

                host.configureBlocking(false);
                System.out.printf("Host socket connected succesfully\n");

                while (!matchCanStart) {

                    int bytesRead = host.read(hostBuffer);
                    if (bytesRead == -1) {
                        host.close();
                        server.close();
                        return;
                    }

                    // No hay nada que leer
                    if (bytesRead == 0)
                        continue;

                    // Comenzar partida si recibimos el codigo del host
                    if (hostBuffer.remaining() == 4) {
                        int code = hostBuffer.getInt();
                        matchCanStart = (code == GameType.GAME_STARTS);
                    }
                }

                host.close();
                server.close();

            } catch (IOException e) {
                System.out.printf("ERROR ON RUN METHOD: %s\n", e.getMessage());
            }

            // Listen until match starts

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
            serverSocket.bind(new InetSocketAddress(GameType.PORT));
            System.out.printf("Servidor esperando cliente...\n");

            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            clientList = new ArrayList<>();

            // Pregame
            hostWantsToStart = false;
            matchCanStart = false;
            hostAllowsMorePlayers = true;
            while (!hostWantsToStart || !matchCanStart) {

                // Accept, receive or send data to current clients
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) { // Client connects
                        handleAccept(key, selector);
                    } else if (key.isReadable()) { // Clients send data to server
                        handleReceive(key);
                    } else if (key.isWritable()) { // Send data to clients
                        handleSend(key);
                    }
                }

            }
        } catch (IOException e) {
            System.out.printf("%s\n", e.getMessage());
            e.printStackTrace();
        } finally {

            if (serverSocket.isOpen()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.printf("Error closing the server socket: %s\n", e.getMessage());
                }
            }
        }

    }

    /**
     * 
     * @param key
     * @param selector
     * @throws IOException
     */
    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {

        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        if (hostAllowsMorePlayers) {
            client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(256));
            clientList.add(client);
            System.out.printf("Nuevo cliente conectado!\n");
        } else {
            client.close();
            System.out.printf("El host no admite a mas clientes, cerrando conexión entrante...\n");
        }
    }

    /**
     * Primer elemento : 1 byte para tipo de dato: INT(0x1) y STRING(0x2)
     * Segundo elemento : 4 bytes para el tamaño en bytes del dato. INT(4 bytes) y
     * STRING(tantos bytes como caracteres)
     * Tercer elemento : Tantos bytes como sean necesarios para los datos
     * 
     * @param key
     * @throws IOException
     */
    private static void handleReceive(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        try {

            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Cliente desconectado y eliminado\n");
                clientList.remove(client);
                client.close();
                return;
            }

            // Modo lectura -> Recibir todos los datos del cliente si es posible, si no,
            // salir y reintentar en la siguiente iteracion
            buffer.flip();
            while (buffer.remaining() >= 5) { // Leer todos los datos si estan disponibles

                buffer.mark();

                byte type = buffer.get();
                if (type == GameType.INTEGER_TYPE) {

                    if (buffer.remaining() < 4) { // Faltan datos -> Guardar estado y esperar
                        buffer.reset();
                        break;
                    }

                    int petitionCode = buffer.getInt();
                    handleClientPetition(key, petitionCode);
                } else if (type == GameType.STRING_TYPE) {

                    if (buffer.remaining() < 4) { // Faltan datos -> Guardar estado y esperar
                        buffer.reset();
                        break;
                    }

                    int size = buffer.getInt();
                    byte value[] = new byte[size];

                    buffer.get(value);
                    String message = new String(value, StandardCharsets.UTF_8);
                    System.out.printf("String recibido: %s\n", message);
                } else {
                    System.out.printf("Tipo de dato no reconocido\n");
                    client.close(); // Muy restrictivo -> MUY provisional
                }
            }
            buffer.compact();

        } catch (SocketException e) {
            clientList.remove(client);
            key.cancel();
            System.out.printf("Ocurrió algun error con el cliente.\nCerrando conexión de forma segura.\n>ERROR: %s\n",
                    e.getMessage());
            System.out.printf("Numero de jugadoes actuales: %d\n", clientList.size());
        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

    }

    /**
     * 
     * @param key
     * @throws IOException
     */
    private static void handleSend(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        try {

            buffer.flip();
            client.write(buffer);
            if (buffer.hasRemaining()) { // Si no todo fue enviado, mantenemos OP_WRITE activo
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            } else { // Todo enviado, podemos dejar de escuchar OP_WRITE
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                buffer.clear();
            }
        } catch (IOException e) {
            System.out.printf("ERROR enviando datos al cliente: %s\n", e.getMessage());
        }

    }

    /**
     * 
     * @param key
     * @param petition
     */
    private static void handleClientPetition(SelectionKey key, final int petition) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        System.out.printf("Peticion recibida: %d\n", petition);
        switch (petition) {
            case GameType.CREATE_PETITION:

                if (allowPlayersServerSocket == null) {

                    try {
                        // Create ServerSocketChannel in any port
                        allowPlayersServerSocket = ServerSocketChannel.open();
                        allowPlayersServerSocket.configureBlocking(false);
                        allowPlayersServerSocket.bind(null);

                        // Escribir nuevo puerto en buffer del cliente creador de partida(host)
                        int port = allowPlayersServerSocket.socket().getLocalPort();
                        buffer.clear();
                        buffer.putInt(port);
                        buffer.flip();

                        // Solicitar envio de datos
                        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);

                        // Listen to accept and send messages from host to allow more players in the
                        // match
                        hostPrivacy = new HostPrivacyThread(allowPlayersServerSocket);
                        allowPlayersThread = new Thread(hostPrivacy);
                        allowPlayersThread.start();
                    } catch (IOException e) {
                        System.out.printf("Error creando host socket: %s\n", e.getMessage());
                    }

                } else {
                    System.out.printf("Ya existe una partida. No se puede crear otra\n");
                }

                break;

            case GameType.JOIN_PETITION:
                break;

            default:

                System.out.printf("Peticion %d no valida\n", petition);
                try {
                    clientList.remove(client);
                    client.close();
                    key.cancel();
                } catch (IOException e) {
                    System.out.printf("ERROR: %s\n", e.getMessage());
                }
                break;
        }
    }

}