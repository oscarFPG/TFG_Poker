package com.ucm;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Poker Game
import com.ucm.control.Controller;
import com.ucm.evaluator.Evaluator;
import com.ucm.middleclasses.DTOClient;
import com.ucm.logic.Game;
import com.ucm.SocketUtils;


public class ServerMain {

    private static int MAX_PLAYERS = Game.NUM_MAX_PLAYERS;

    private static ServerSocket _serverSocket;

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
            System.out.printf("Socket servidor creado en el puerto %d\n", GameType.PORT);

            while(true){

                List<DTOClient> clientList = preGame();

                if(!clientList.isEmpty())
                    game(clientList);
            }
        }
        catch(IOException e){
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

    }

    public static List<DTOClient> preGame(){

        List<DTOClient> _sockets = new ArrayList<>();
        Random rand = new Random();
        int matchID = rand.nextInt();

        try {

            // Keep waiting for more players while limit not reached
            while (_sockets.size() < MAX_PLAYERS) {

                Socket clientSocket = _serverSocket.accept();

                String playerName = SocketUtils.receiveString( clientSocket.getInputStream() );
                DTOClient newClient = new DTOClient(_sockets.size(), matchID, playerName, clientSocket);

                _sockets.add( newClient );
            }


            // Notify all players if they are or not the admin player -> Always the first connected, first on the list
            System.out.printf("Mandando permisos de clientes\n");
            Socket adminSocket = _sockets.get(0).socket();
            for(DTOClient client : _sockets){

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
                SocketUtils.sendInteger(_sockets.get(i).socket().getOutputStream(), GameType.START_GAME);
            }

        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

        return _sockets;
    }

    public static void game(final List<DTOClient> clients) throws IOException{

        Game game = new Game(clients);
        Controller controller = new Controller(game);
        controller.run();
    }

}