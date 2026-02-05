package com.ucm;

// GUI
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMain extends Application {
    
    private static ByteBuffer _buffer;

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

        //launch(args);
        try{
            SocketChannel socket = SocketChannel.open( new InetSocketAddress("localhost", GameType.PORT) );
            System.out.printf("SocketChannel creado en el puerto %d\n", GameType.PORT);

            socket.close();
        }
        catch(IOException e){
            System.out.printf("Error creating SocketChannel: %s\n", e.getMessage());
        }

        /*
        try{

            Socket socket = new Socket(host, port);
            System.out.printf("Socket cliente creado en el puerto %d\n", port);

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
        */
    }


    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }
}
 