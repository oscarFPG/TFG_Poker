package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.lang.Thread;

// GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMain extends Application {

    private static String hostname = "localhost";
    private static String name;
    private static Scanner scanner;

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * Run:
     *      .\mvnw.cmd -pl client -Prun exec:java
     * Debug:
     *      .\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) {

        SocketChannel socket;
        ByteBuffer readBuffer, writeBuffer;
        try{
            socket = SocketChannel.open();
            socket.configureBlocking(false);

            socket.connect( new InetSocketAddress(hostname, GameType.PORT) );
            System.out.printf("Client connected!\n");
            
            // Check if SocketChannel is already connected and avoid busy wait
            while(!socket.finishConnect()){
                Thread.sleep(500);
            }

            // Get clients petition
            scanner = new Scanner(System.in);
            System.out.printf("Write your username: ");
            name = scanner.next();

            System.out.printf("1-Create(C)\n2-Join(J)\nWrite option name: ");
            String input = scanner.next();
            scanner.close();

            // Send client petition
            if(input.equalsIgnoreCase("create") || input.equalsIgnoreCase("c")){

                writeBuffer = ByteBuffer.allocate(1 + Integer.BYTES);   // 1 byte de tipo + 4 bytes de integer(tamaño string) + integer

                writeBuffer.put(GameType.INTEGER_TYPE);
                writeBuffer.putInt(GameType.CREATE_PETITION);

                writeBuffer.flip();
                socket.write(writeBuffer);

                // Recibir nuevo puerto de partida
                readBuffer = ByteBuffer.allocate(Integer.BYTES);
                readBuffer.clear();

                int bytesRead = 0;
                while(readBuffer.hasRemaining()){
                    bytesRead = socket.read(readBuffer);
                }
                readBuffer.flip();
            
                int puerto = readBuffer.getInt();
                System.out.printf("Nuevo puerto es %d\n", puerto);
            }
            else if(input.equalsIgnoreCase("join") || input.equalsIgnoreCase("j")){

                writeBuffer = ByteBuffer.allocate(1 + Integer.BYTES);   // 1 byte de tipo + 4 bytes de integer(tamaño string) + integer

                writeBuffer.put(GameType.INTEGER_TYPE);
                writeBuffer.putInt(GameType.JOIN_PETITION);

                writeBuffer.flip();
                socket.write(writeBuffer);
            }
            else{
                System.out.printf("Petition not found\n");
            }

            while(true){}
            //socket.close();
        }
        catch(NoSuchElementException e){    // El programa termina mientras se tiene el Scanner abierto y no se escribe nada
            scanner.close();
        }
        catch(IOException | InterruptedException e){
            System.out.printf("ERROR: %d\n", e.getMessage());
        }

        //launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }
}