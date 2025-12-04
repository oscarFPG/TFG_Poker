package com.ucm;

// GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Socket Utils
import com.ucm.SocketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.CodeSigner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain extends Application {

    public static String _host = "localhost";
    private static int Cards[] = new int[4];
    private static int Board_Cards[] = new int[10];
    private static String _playerName = "User";

    private static Socket _socket; 
    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * Run:
     *      .\mvnw.cmd -pl client -Prun exec:java
     * Debug:
     *      .\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) {

        // Connect to server
        try{
           _socket = new Socket(_host, GameType.PORT);
            System.out.printf("Socket cliente creado en el puerto %d\n", GameType.PORT);
        }
        catch(IOException e){
            System.out.printf("ERROR: %s\n", e.getMessage());
            System.out.printf("Terminating program!\n");
            return;
        }
        
        // Get user petition to the server
        Scanner scanner = new Scanner(System.in);
        boolean opcionValida = false;
        int opcion = -1;
        while(!opcionValida){

            System.out.flush();
            System.out.printf("Seleccione que desea hacer: \n");
            System.out.printf("0: Crear una nueva partida\n");
            System.out.printf("1: Unirme a una nueva partida\n");
            System.out.printf("2: Reconectarme a una partida en curso\n");

            opcion = -1;
            try{
                opcion = scanner.nextInt();
            }
            catch(InputMismatchException e){
                System.out.printf("ERROR: introduzca un numero valido\n");
            }

            opcionValida = (0 <= opcion && opcion <= 2); 
        }
        scanner.close();

        boolean success = false;
        if(opcion == 0){

            success = sendPetitionCode(GameType.PETITION_CREATE_MATCH);
            
            int newPort = -1;
            try{
                newPort = SocketUtils.receiveInt( _socket.getInputStream() );
                System.out.printf("Receiving new port %d\n", newPort);
                _socket.close();
            }
            catch(IOException e){
                return;
            }
            createPokerRoom(newPort);
        }
        else if(opcion == 1){

            success = sendPetitionCode(GameType.PETITION_JOIN_MATCH);
            joinPokerRoom();
        }
        else if(opcion == 2){

            success = sendPetitionCode(GameType.PETITION_RECONNECT_MATCH);
            reconnectPokerRoom();
        }

        cleanup();
    }

    private static boolean sendPetitionCode(final int code){

        // Send petition code to server
        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), code);
        }
        catch(IOException e){
            return false;
        }

        return true;
    }

    private static void createPokerRoom(final int port){

        int isAdmin = -1;
        int gameStart = -1;

        Scanner scanner = new Scanner(System.in);
        try {

            _socket = new Socket(_host, port);                        
            System.out.printf("Socket created on port %d\n", port);
            
            String message = SocketUtils.receiveString(_socket.getInputStream());
            System.out.printf("Server message: [%s]\n", message);
            /*
            // Ask client's name
            System.out.printf("Introduce tu nombre: \n");
            _playerName = scanner.nextLine();
        
            // Send playername to server
            SocketUtils.sendString(_socket.getOutputStream(), _playerName);

            // Receive server to tell client if its an admin
            isAdmin = SocketUtils.receiveInt(_socket.getInputStream());

            if(isAdmin == GameType.PLAYER_IS_ADMIN){

                System.out.printf("Esperando a que todos los jugadores se unan...\n");
                System.out.printf("Pulsa ENTER para comenzar la partida\n");
                scanner.nextLine();

                // Send order START_GAME to server
                SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_START_ADMINISTRATOR);
            }
            else if(isAdmin == GameType.PLAYER_NOT_ADMIN) {
                System.out.printf("Esperando a comenzar la partida...\n");
            }

            // Wait for server GAME_START signal
            gameStart = SocketUtils.receiveInt(_socket.getInputStream());
            System.out.printf("Comenzando partida!\n");
            
            // Match starts
            */

            scanner.close();
            _socket.close();
        }
        catch (IOException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

    }

    private static void joinPokerRoom(){
        // TODO
    }

    private static void reconnectPokerRoom(){
        // TODO
    }

    private static void cleanup(){

        // Shut down server
        try{
            if(_socket != null && !_socket.isClosed()){
                _socket.close();
            }
        }
        catch(IOException e){
            System.out.printf("Shutting down server!\n");
        }
    }


    private void logic(){
        
        //launch(args);
        try{

            Socket socket = new Socket(_host, GameType.PORT);

            OutputStream output = socket.getOutputStream(); // Send buffer
            InputStream input = socket.getInputStream();    // Receive buffer

            // Recibir mensaje
            String message = SocketUtils.receiveString(input);
            System.out.printf("Mensaje recibido del servidor: %s\n", message);

            // Solicitar unirse a patida
            SocketUtils.sendInteger(output, GameType.ESTABLISH_CONECTION);

            // Mensaje unido a partida
            int conection = SocketUtils.receiveInt(input);
            if (conection == GameType.CONECTION_ACEPTED) {
                System.out.print("Servidor ha aceptado la conexion");
            } 
            // Mensaje no unido a partida
            else if ( conection == GameType.CONECTION_DECLINE){
                System.out.print("Servidor ha rechazado la conexion") ;
                return;
            }

            boolean endOfGame = false;
            while (!endOfGame){
                boolean endOfHand = false;
                
                //NO ES SHOW DOWN
                while ( !endOfHand){
                     boolean endOfRound = false;
                    //RONDA DE APUESTAS, HASTA QUE NO ACABE, NO SE REPARTEN LAS SIGUIENTES CARTAS EN LA MESA
                    while( !endOfRound){
                        int action = SocketUtils.receiveInt(input);
                        //AVISAR TURNO DE JUGADOR
                        switch (action) {
                            case GameType.GAME_STARTS:
                                    try {
                                    // Repartir dos cartas
                                    for (int i = 0; i < 4; i++) {
                                        Cards[i] = SocketUtils.receiveInt(input);
                                    }
                                    System.out.println("Cartas recibidas: " + Arrays.toString(Cards));
                                } catch (IOException e) {
                                    System.out.println("Error al recibir cartas: " + e.getMessage());
                                }
                                break;
                            case GameType.TURN_WAIT:
                                System.out.println("Usuario esperando a que sea su turno");
                                break;
                            case GameType.TURN_PLAY:
                                //ACCION DEL JUGADOR
                                System.out.println("Usuario a jugado: FOLD");
                                SocketUtils.sendInteger(output, GameType.FOLD);
                                break;
                            case GameType.END_OF_ROUND:
                                System.out.println("Fin de la ronda de apuestas");
                                endOfRound = true;
                                break;
                            case GameType.END_OF_HAND: //ES SHOW_DOWN
                                System.out.println("Fin de la mano");
                                endOfRound = true;
                                endOfHand= true;
                                break;
                            case GameType.END_OF_GAME:
                                System.out.println("El juego ha acabado");
                                endOfRound = true;
                                endOfHand= true;
                                endOfGame = true;
                                break;
                        }
                    }
                    //prueba
                
                    //REPARTIR CARTAS DE LA MESA
                    if ( !endOfHand && !endOfGame){
                        int fase = SocketUtils.receiveInt(input);
                        int actual_board_cards = (fase == GameType.PRE_FLOP) ? 6 : 2;
                        for (int i = 0; i < actual_board_cards; i++) {
                            Board_Cards[i] = SocketUtils.receiveInt(input);
                        }
                        System.out.println("Cartas en la mesa recibidas: " + Arrays.toString(Board_Cards));
                    }
                }
                

                if (!endOfGame && endOfHand){
                    int hand_winner = SocketUtils.receiveInt(input);
                    if (hand_winner == GameType.HAND_OVER_WIN){
                        System.out.println("Usuario ha ganado esta mano");
                    }
                    else if (hand_winner == GameType.HAND_OVER_LOSE){
                        System.out.println("Usuario ha perdido esta mano");
                    }
                }

            }

            int game_winner = SocketUtils.receiveInt(input);
            if (game_winner == GameType.GAME_OVER_WIN){
                System.out.println("Usuario ha ganado el juego!");
            }
            else if (game_winner == GameType.GAME_OVER_LOSE){
                System.out.println("Usuario ha perdido el juego");
            
            }

            socket.close();
        }
        catch(IOException e){
            System.out.printf("ERROR: %s\n", e.getMessage());
        }

    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }
}