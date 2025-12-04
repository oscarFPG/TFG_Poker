package com.ucm;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.ServerRef;

// Poker Game
import com.ucm.control.Controller;
import com.ucm.evaluator.Evaluator;
import com.ucm.game.PokerRoom;
import com.ucm.middleclasses.DTOClient;
import com.ucm.logic.Game;
import com.ucm.SocketUtils;


public class ServerMain {

    public static int MAX_PLAYERS = Game.NUM_MAX_PLAYERS;

    private static ServerSocket _serverSocket;
    private static List<PokerRoom> _gamesList;

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * Run:
     *      .\mvnw.cmd -pl server -Prun exec:java
     * Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * 
     * Run the Tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) {

        try{

            Evaluator ev = Evaluator.getInstance();

            _serverSocket = new ServerSocket(GameType.PORT);
            _gamesList = new ArrayList<PokerRoom>();

            System.out.printf("Socket server created on port %d\n", GameType.PORT);
            while(true){

                Socket newClient = _serverSocket.accept();
                int clientPetition = SocketUtils.receiveInt( newClient.getInputStream() );

                if(clientPetition == GameType.PETITION_CREATE_MATCH){
                    ServerSocket newServerSocket = new ServerSocket(0);
                    SocketUtils.sendInteger(newClient.getOutputStream(), newServerSocket.getLocalPort());
                    
                    System.out.printf("Sending port %d\n", newServerSocket.getLocalPort());
                    createPokerRoom(newServerSocket);
                    break;
                }
                else if(clientPetition == GameType.PETITION_JOIN_MATCH){
                    joinPokerRoom(newClient);
                }
                else if(clientPetition == GameType.PETITION_RECONNECT_MATCH){
                    reconnectPokerRoom(newClient);
                }
                else{
                    SocketUtils.sendInteger(newClient.getOutputStream(), GameType.PETITION_UNKNOWN);
                    newClient.close();
                }
            }

        }
        catch(IOException e){   // Handle errors when creating main Server Socket or Evaluator -> Terminate program
            System.out.printf("ERROR: %s\n", e.getMessage() );
        }

        cleanup();
    }

    private static void createPokerRoom(ServerSocket server) throws IOException{
        
        String uniqueID = UUID.randomUUID().toString();
        //List<DTOClient> clientList = preGame(server);

        Socket client = server.accept();
        SocketUtils.sendString(client.getOutputStream(), "Partida creada");
        server.close();
        client.close();

        /*
        try{
            if(!clientList.isEmpty()){
                _gamesList.add( new PokerRoom(uniqueID, server, clientList) );
            }
            else
                System.out.printf("Partida creada!\n");
        }
        catch(IOException e){
            System.out.printf("ERROR: %s\n", e.getMessage());
        }
        */
    }

    private static List<DTOClient> preGame(ServerSocket server){

        List<DTOClient> clientSocketList = new ArrayList<>();
        Random rand = new Random();
        int matchID = rand.nextInt();

        try {

            // Keep waiting for more players while limit not reached
            while (clientSocketList.size() < MAX_PLAYERS) {

                Socket clientSocket = server.accept();

                String playerName = SocketUtils.receiveString( clientSocket.getInputStream() );
                DTOClient newClient = new DTOClient(clientSocketList.size(), matchID, playerName, clientSocket);

                clientSocketList.add( newClient );
            }

            // Notify all players if they are or not the admin player -> Always the first connected, first on the list
            System.out.printf("Mandando permisos de clientes\n");
            Socket adminSocket = clientSocketList.get(0).socket();
            for(DTOClient client : clientSocketList){

                Socket currentSocket = client.socket();
                if(currentSocket == adminSocket)
                    SocketUtils.sendInteger(currentSocket.getOutputStream(), GameType.PLAYER_IS_ADMIN);
                else
                    SocketUtils.sendInteger(currentSocket.getOutputStream(), GameType.PLAYER_NOT_ADMIN);
            }

            // Wait for administrator to start the match
            System.out.printf("Esperando al admin para comenzar\n");
            int code = 0;
            do {
                code = SocketUtils.receiveInt(adminSocket.getInputStream());
            } while (code != GameType.GAME_START_ADMINISTRATOR);

            // Notify all players the game started
            System.out.printf("Admin ha comenzado la partida!\n");
            for(int i = 0; i < MAX_PLAYERS; i++){
                SocketUtils.sendInteger(clientSocketList.get(i).socket().getOutputStream(), GameType.START_GAME);
            }

        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

        return clientSocketList;
    }

    private static void joinPokerRoom(Socket client){

    }

    private static void reconnectPokerRoom(Socket client){

    }

    private static void cleanup(){

        // Shut down server
        try{
            if(_serverSocket != null && !_serverSocket.isClosed()){
                _serverSocket.close();
            }
        }
        catch(IOException e){
            System.out.printf("Shutting down server!\n");
        }
    }

}