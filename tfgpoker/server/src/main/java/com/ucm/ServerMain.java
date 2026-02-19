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

    private static class ClientStruct {

        public String clientName;
        public SocketChannel clientSocket;

        ClientStruct(final String name, final SocketChannel s){
            clientName = name;
            clientSocket = s;
        }
    }


    public static int MAX_PLAYERS = 9;

    private static Selector _selector;
    private static ServerSocketChannel _serverSocket;
    private static List<ClientStruct> _roomList;
    private static SocketChannel host;

    private static boolean _hostWantsToStart;
    
    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * Run:
     *      .\mvnw.cmd -pl server -Prun exec:java
     * Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * Run the Tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) {

        try {
            _serverSocket = ServerSocketChannel.open();
            _serverSocket.configureBlocking(false);
            _serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            System.out.printf("Server up, waiting for clients...\n");

            _selector = Selector.open();
            _serverSocket.register(_selector, SelectionKey.OP_ACCEPT);

            _roomList = new ArrayList<>();

            _hostWantsToStart = false;
            while (!_hostWantsToStart) {

                // Accept, receive or send data to current clients
                _selector.select();
                Iterator<SelectionKey> keys = _selector.selectedKeys().iterator();
                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid())
                        continue;


                    if (key.isAcceptable()) {       // Client connects
                        handleAccept(key, _selector);
                    } 
                    else if (key.isReadable()) {    // Clients send data to server
                        handleReceive(key);
                    } 
                    else if (key.isWritable()) {    // Send data to clients
                        handleSend(key);
                    }
                }

            }
            System.out.printf("Host empieza la partida!\n");

        } 
        catch (IOException e) {
            System.out.printf("%s\n", e.getMessage());
        } 
        finally {

            if (_serverSocket.isOpen()) {
                try {
                    _serverSocket.close();
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

        try{

            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {	// Conexion cerrada
				key.cancel();
                client.close();
                return;
            }

            if (bytesRead == 0) {	// Nada que leer
                return;
            }

            buffer.flip();

            byte tipo;
            while (buffer.remaining() >= 5) {

                buffer.mark();

				// Leer datos en base al tipo de dato
				tipo = buffer.get();
                switch (tipo) {
                case GameType.INTEGER_TYPE:
                    int valor = buffer.getInt();
                    System.out.println("Nombre recibido: " + valor);
                    break;

                case GameType.NAME_TYPE:

					int size = buffer.getInt();
                    byte[] strBytes = new byte[size];
    				buffer.get(strBytes);

    				String nombre = new String(strBytes, StandardCharsets.UTF_8);
                    System.out.printf("Cliente %s autenticado!\n", nombre);

					// Almacenar nombre para relacionar socket-nombre si esta en la lista de jugadores
					for(ClientStruct cs : _roomList)
						if(cs.clientSocket == client)
							cs.clientName = String.copyValueOf(nombre.toCharArray());

                    break;

                case GameType.PETITION_TYPE:
                    int code = buffer.getInt();
                    handleClientPetition(key, code);
                    break;

                default:
                    System.out.println("Tipo desconocido: " + tipo);
                }
            }
            buffer.compact();

        }
		catch(SocketException e){
			ClientStruct toRemove = null;
			for(ClientStruct cs : _roomList)
				if(cs.clientSocket == client)
					toRemove = cs;

			if(toRemove != null)
				_roomList.remove(toRemove);
			key.cancel();
			System.out.printf("Cerrando conexion con cliente desconectado: %s\n", e.getMessage());
		}
        catch(IOException e){
            System.out.printf("Error recibiendo datos %s\n", e.getMessage());
			try {
				key.cancel();
				client.close();
			}
			catch (IOException ignored) {}
        }

    }

    private static void handleSend(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

    }

    private static void handleClientPetition(SelectionKey key, final int petition) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
		
        // Gestionar peticiones del host de forma especial(START GAME)
        if(client == host){
            System.out.printf("El host quiere empezar! %d\n", petition);
            _hostWantsToStart = true;
        }
        else{

            switch (petition) {
            case GameType.CREATE_PETITION:
                System.out.printf("Peticion CREATE del cliente\n");
                if(_roomList.isEmpty()){

                    System.out.printf("Creando partida!\n");
                    _roomList.add( new ClientStruct(null, client) );
                    host = client;
                }
                else{
                    System.out.printf("Partida ya existente!\n");
                    try {
                        client.close();
                    }
                    catch (IOException e) {
                        System.out.printf("Error cerrando conexion con el cliente %s\n", e.getMessage());
                    }
                }
                break;

            case GameType.JOIN_PETITION:
                System.out.printf("Peticion JOIN del cliente\n");

                // No esta vacia(No ha sido creada) y no esta llena
                if(!_roomList.isEmpty() && _roomList.size() < MAX_PLAYERS){
                    _roomList.add( new ClientStruct(null, client) );
                    System.out.printf("Uniendote a partida!\n");
                }
                else{

                    System.out.printf("Partida no creada o llena!\n");
                    try {
                        client.close();
                        key.cancel();
                    }
                    catch(IOException e) {
                        System.out.printf("Error intentando cerrar conexión de forma segura: %s\n", e.getMessage());
                    }
                }
                break;
        
            default:
                System.out.printf("Peticion desconocida %d\n", petition);
                break;
            }
        }
    }

}