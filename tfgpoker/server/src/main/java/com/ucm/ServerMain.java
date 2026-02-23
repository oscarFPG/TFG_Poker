package com.ucm;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    private static class ClientStructPreGame {

        public String clientName;
        public SocketChannel clientSocket;
        public SelectionKey key;

        ClientStructPreGame(final String name, final SocketChannel s, final SelectionKey k){
            clientName = name;
            clientSocket = s;
            key = k;
        }
    }

    public static class ClientStructGame {

        public String clientName;
        public Socket clientSocket;

        ClientStructGame(final String name, final Socket s){
            clientName = name;
            clientSocket = s;
        }
    }


    public static int MAX_PLAYERS = 9;
    private static SocketChannel _host;
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
    public static void main(String[] args) throws IOException {

        List<ClientStructPreGame> joinedClients = preGame();

        // Convert from non-blocking SocketChannel to blocking Socket
        List<ClientStructGame> players = new ArrayList<>();
        for(ClientStructPreGame cs : joinedClients){
            cs.clientSocket.configureBlocking(true);
            players.add( new ClientStructGame(cs.clientName, cs.clientSocket.socket()) );
        }

        game(players);
    }

    // ------------------------ Pregame phase ------------------------

    private static List<ClientStructPreGame> preGame(){

        List<ClientStructPreGame> roomList = new ArrayList<>();     // Player list that enter the game
        List<ClientStructPreGame> clientList = new ArrayList<>();   // Client list that tries to play
        ServerSocketChannel serverSocket = null;
        Selector selector = null;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind( new InetSocketAddress(GameType.PORT) );
            System.out.printf("Server up, waiting for clients...\n");

            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            _hostWantsToStart = false;
            while (!_hostWantsToStart) {

                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid())
                        continue;


                    if (key.isAcceptable()) {
                        handleAccept(key, selector);
                    } 
                    else if (key.isReadable()) {
                        handleReceive(key, clientList, roomList);
                    } 
                    else if (key.isWritable()) {
                        handleSend(key);
                    }
                }
            }
            System.out.printf("Host empieza la partida!\n");

            // Cancel all clients keys -> Important!
            for (SelectionKey key : selector.keys()) {
                key.cancel();
            }
            selector.selectNow();

            // Send to all clients the GAME_STARTS flag
            ByteBuffer broadcastBuffer = ByteBuffer.allocate(Integer.BYTES);
            broadcastBuffer.putInt(GameType.GAME_STARTS);
            for(ClientStructPreGame cs : roomList){
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

            //  Eliminate all not in-game players to avoid infinite waiting
            for(ClientStructPreGame cs : clientList){
                try{
                    cs.key.cancel();
                    cs.clientSocket.close();
                }
                catch(IOException ignored){}
            } 
        } 
        catch (IOException e) {
            System.out.printf("%s\n", e.getMessage());
        } 
        finally {

            if (serverSocket.isOpen()) {
                try {
                    serverSocket.close();
                    selector.close();
                }
                catch (IOException e) {
                    System.out.printf("Error closing the server socket: %s\n", e.getMessage());
                }
            }
        }

        return roomList;
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {

        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(128));
        System.out.printf("Nuevo cliente conectado!\n");
    }

    private static void handleReceive(SelectionKey key, List<ClientStructPreGame> clientList, List<ClientStructPreGame> roomList) {

        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead;
        byte tipo;

        try{

            bytesRead = socket.read(buffer);
            if (bytesRead == -1) {
                key.cancel();
                socket.close();
                System.out.printf("Connection closed!\n");
                return;
            }

            buffer.flip();
            tipo = buffer.get();
            System.out.printf("Tipo de petición %d\n", tipo);

            switch (tipo) {
            case GameType.DATA_TYPE_NAME:
                int msgSize;
                byte[] msgBytes;

                msgSize = buffer.getInt();
                msgBytes = new byte[msgSize];
                buffer.get(msgBytes);
                String clientName = new String(msgBytes, StandardCharsets.UTF_8);
                System.out.printf("Client %s authenticated!\n", clientName);

                // Almacenar nombre para relacionar socket-nombre si esta en la lista de clientes
                // Esta lista es distinta a la lista de jugadores que SI que van a entrar a partida
                System.out.printf("Cliente %s almacenado en la lista de clientes!\n", clientName);
                clientList.add( new ClientStructPreGame(clientName, socket, key) );

                break;

            case GameType.DATA_TYPE_PETITION:
                int code = buffer.getInt();
                System.out.printf("Petition type received %d\n", code);
                handleClientPetition(key, code, clientList, roomList);

                break;

            default:
                System.out.println("Tipo desconocido: " + tipo);
            }
        }
		catch(SocketException e){

            System.out.printf("Cerrando conexion con cliente desconectado: %s\n", e.getMessage());
			ClientStructPreGame toRemove = roomList.stream()
                .filter(c -> c.clientSocket == socket)
                .findFirst().orElse(null);

			if(toRemove != null){
                roomList.remove(toRemove);
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

    private static void handleClientPetition(SelectionKey key, final int petition, List<ClientStructPreGame> clientList, List<ClientStructPreGame> roomList) {

        SocketChannel client = (SocketChannel) key.channel();
		
        switch (petition) {
        case GameType.CREATE_PETITION:

            System.out.printf("Peticion CREATE del cliente\n");
            if(roomList.isEmpty()){

                System.out.printf("Creando partida!\n");
                ClientStructPreGame hostClient = clientList.stream()
                    .filter(c -> c.clientSocket == client).findFirst().
                    orElse(null);

                if(hostClient != null){
                    clientList.remove(hostClient);
                    roomList.add(hostClient);
                    _host = client;  // Select the host to wait for his petition to start the game
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

            System.out.printf("Peticion JOIN del cliente\n");
            if(!roomList.isEmpty() && roomList.size() < MAX_PLAYERS){     // La partida ha sido creada y ha, al menos, dos jugadores

                ClientStructPreGame joiningClient = clientList.stream()
                    .filter(c -> c.clientSocket == client).findFirst().
                    orElse(null);

                if(joiningClient != null){
                    roomList.add( joiningClient );
                    clientList.removeIf(c -> c.clientSocket == joiningClient.clientSocket);
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

            // Other client except the true host tries to start the game
            if(client != _host){

                System.out.printf("Only the host can start the game!\n");
                try{
                    ClientStructPreGame cs = roomList.stream()
                        .filter(c -> c.clientSocket == client).findFirst().orElse(null);

                    clientList.remove(cs);
                    roomList.remove(cs);
                    key.cancel();
                    client.close();
                }
                catch(IOException ignored){}
            }

            if(roomList.size() >= 2){
                System.out.printf("Host starts the game succesfully with %d players!\n", roomList.size());
                _hostWantsToStart = true;
            }
            else{
                System.out.printf("Cannot start the game with less than 2 players!\n");
                // TODO: Aqui deberia enviar codigo para que el cliente se mantenga esperando y enviandonos el codigo hasta que haya al menos 2 jugadores
                // key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
            }
            
            break;

        default:
            System.out.printf("Unknown petition %d\n", petition);
            break;
        }
        
    }


    // ------------------------ Game phase ------------------------

    private static void game(List<ClientStructGame> clients){
        
    }

}