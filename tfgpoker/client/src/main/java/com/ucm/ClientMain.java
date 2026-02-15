package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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

        SocketChannel socket;
        ByteBuffer buffer;
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
            Scanner scanner = new Scanner(System.in);
            System.out.printf("Write your username: ");
            name = scanner.next();

            System.out.printf("1-Create(C)\n2-Join(J)\nWrite option name: ");
            String input = scanner.next();
            scanner.close();

            // Send client petition
            if(input.equalsIgnoreCase("create") || input.equalsIgnoreCase("c")){

                byte[] texto = input.getBytes();
                buffer = ByteBuffer.allocate(1 + 4 + input.length());   // 1 byte de tipo + 4 bytes de integer(tamaño string) + input.lenght() bytes 

                buffer.put(GameType.STRING_TYPE);
                buffer.putInt(input.length());
                buffer.put(texto);

                buffer.flip();
                socket.write(buffer);
            }
            else if(input.equalsIgnoreCase("join") || input.equalsIgnoreCase("j")){

                byte[] texto = input.getBytes();
                buffer = ByteBuffer.allocate(1 + 4 + input.length());

                buffer.put(GameType.STRING_TYPE);
                buffer.putInt(input.length());
                buffer.put(texto);

                buffer.flip();
                socket.write(buffer);
            }
            else{
                System.out.printf("Petition not found\n");
            }

            socket.close();
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