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
        public SelectionKey key;

        ClientStruct(final String name, final SocketChannel s, final SelectionKey k){
            clientName = name;
            clientSocket = s;
            key = k;
        }
    }


    public static int MAX_PLAYERS = 9;

    private static Selector _selector;
    private static ServerSocketChannel _serverSocket;
    private static List<ClientStruct> _roomList;
    private static List<ClientStruct> _clientList;
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
            _clientList = new ArrayList<>();
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

            // Notify all players that game has started
            ByteBuffer broadcastBuffer = ByteBuffer.allocate(Integer.BYTES);
            broadcastBuffer.putInt(GameType.GAME_STARTS);
            for(ClientStruct cs : _roomList){
                try{
                    broadcastBuffer.rewind();
                    cs.clientSocket.write(broadcastBuffer);
                }
                catch(IOException e){
                    System.out.printf("Error sending GAME_STARTS flag: %s\n", e.getMessage());
                    try{
                        cs.clientSocket.close();
                    }
                    catch(IOException exception){
                        System.out.printf("Error closing socket: %s\n", e.getMessage());
                    }
                }
            }

            //  Eliminate all not in-game players
            for(ClientStruct cs : _clientList){
                try{
                    cs.key.cancel();
                    cs.clientSocket.close();
                }
                catch(IOException ignored){}
            }

            System.out.printf("Comienza la partida!\n");
            while(true){}

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

        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(128));
        System.out.printf("Nuevo cliente conectado!\n");
    }

    private static void handleReceive(SelectionKey key) {

        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead;
        byte tipo;

        try{

            bytesRead = socket.read(buffer);
            if (bytesRead == -1) {	// Conexion cerrada
                key.cancel();
                socket.close();
                System.out.printf("Conexion cerrada\n");
                return;
            }

            // Leer el tipo de peticion
            buffer.flip();
            tipo = buffer.get();
            System.out.printf("Tipo de petición %d\n", tipo);

            // leer posibles datos adicionales segun el tipo de peticion
            switch (tipo) {
            case GameType.DATA_TYPE_NAME:
                int size;
                byte[] strBytes;

                // Get string size and string value
                size = buffer.getInt();
                strBytes = new byte[size];
                buffer.get(strBytes);
                String nombre = new String(strBytes, StandardCharsets.UTF_8);
                System.out.printf("Cliente %s autenticado!\n", nombre);

                // Almacenar nombre para relacionar socket-nombre si esta en la cola de jugadores
                System.out.printf("Cliente %s almacenado en la cola!\n", nombre);
                _clientList.add( new ClientStruct(nombre, socket, key) );

                break;

            case GameType.DATA_TYPE_PETITION:
                int code;

                code = buffer.getInt();
                System.out.printf("Petition type received %d\n", code);
                handleClientPetition(key, code);
                break;

            default:
                System.out.println("Tipo desconocido: " + tipo);
            }
        }
		catch(SocketException e){

            System.out.printf("Cerrando conexion con cliente desconectado: %s\n", e.getMessage());
			ClientStruct toRemove = _roomList.stream()
                .filter(c -> c.clientSocket == socket)
                .findFirst().orElse(null);

			if(toRemove != null){
                _roomList.remove(toRemove);
                try{
                    socket.close();
                    key.cancel();
                }
                catch(IOException ignored){}
            }
		}
        catch(IOException e){
            System.out.printf("Error recibiendo datos %s\n", e.getMessage());
			try {
				key.cancel();
				socket.close();
			}
			catch (IOException ignored) {}
        }

        buffer.clear();
    }

    private static void handleSend(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

    }

    private static void handleClientPetition(SelectionKey key, final int petition) {

        SocketChannel client = (SocketChannel) key.channel();
		
        // Gestionar peticiones del host de forma especial(START GAME)
        switch (petition) {
        case GameType.CREATE_PETITION:

            System.out.printf("Peticion CREATE del cliente\n");
            if(_roomList.isEmpty()){

                System.out.printf("Creando partida!\n");
                ClientStruct hostClient = _clientList.stream()
                    .filter(c -> c.clientSocket == client).findFirst().
                    orElse(null);

                if(hostClient != null){
                    _clientList.remove(hostClient);     // Remove from clients queue
                    _roomList.add(hostClient);          // Add to in-game players
                    host = client;  // Select the host to wait for his petition to start the game
                    System.out.printf("Client %s selected as host\n", hostClient.clientName);
                }
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

            // No esta vacia(No ha sido creada) y no esta llena
            System.out.printf("Peticion JOIN del cliente\n");
            if(!_roomList.isEmpty() && _roomList.size() < MAX_PLAYERS){

                ClientStruct joiningClient = _clientList.stream()
                    .filter(c -> c.clientSocket == client).findFirst().
                    orElse(null);

                if(joiningClient != null){
                    _roomList.add( joiningClient );
                    System.out.printf("Uniendote a partida!\n");
                }
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
    
        case GameType.HOST_START_GAME_PETITION:

            // Other client except the tru host tries to start the game
            if(client != host){

                System.out.printf("Only the host can start the game!\n");
                try{
                    ClientStruct cs = _roomList.stream()
                        .filter(c -> c.clientSocket == client).findFirst().orElse(null);

                    _clientList.remove(cs);
                    _roomList.remove(cs);
                    key.cancel();
                    client.close();
                }
                catch(IOException ignored){}
            }

            //  If there is at leats 2 players in the game it can start
            if(_roomList.size() >= 2){
                System.out.printf("Host starts the game succesfully with %d players!\n", _roomList.size());
                _hostWantsToStart = true;
            }
            else{
                System.out.printf("Cannot start the game with less than 2 players!\n");
                // TODO: Aqui deberia enviar codigo para que el cliente se mantenga esperando y enviandonos el codigo hasta que haya al menos 2 jugadores
            }
            
            break;

        default:
            System.out.printf("Unknown petition %d\n", petition);
            break;
        }
        
    }

}