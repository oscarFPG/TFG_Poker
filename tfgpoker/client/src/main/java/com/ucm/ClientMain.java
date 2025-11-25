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
import java.util.Scanner;

public class ClientMain extends Application {

    public static String host = "localhost";
    public static int port = 5005;

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * Run:
     * .\mvnw.cmd -pl client -Prun exec:java
     * Debug:
     * .\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     * .\mvnw.cmd test
     */
    public static void main(String[] args) {
        preGame();
        
    }

    public static void preGame(){
        // launch(args);
        int isAdmin = -1;
        int gameStart = -1;

        try {

            Socket socket = new Socket(host, port);
            System.out.printf("Socket cliente creado en el puerto %d\n", port);

            isAdmin = SocketUtils.receiveInt(socket.getInputStream());
            if(isAdmin == GameType.PLAYER_IS_ADMIN){

                System.out.printf("Esperando a que todos los jugadores se unan...\n");
                System.out.printf("Pulsa ENTER para comenzar la partida\n");
                Scanner sc = new Scanner(System.in);
                sc.nextLine();
                sc.close();
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
                
                SocketUtils.sendInteger(socket.getOutputStream(), GameType.GAME_START_ADMINISTRATOR);
            }
            else if(isAdmin == GameType.PLAYER_NOT_ADMIN) {
                System.out.printf("Esperando a comenzar la partida...\n");
            }

            gameStart = SocketUtils.receiveInt(socket.getInputStream());
            System.out.printf("Comenzando partida!\n");

            // La partida comienza

            socket.close();
        } catch (IOException e) {
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