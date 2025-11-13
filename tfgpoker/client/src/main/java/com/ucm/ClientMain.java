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


public class ClientMain extends Application {
    
    public static String host = "localhost";
    public static int port = 5005;

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

            Socket socket = new Socket(host, port);
            System.out.printf("Socket cliente creado en el puerto %d\n", port);

            OutputStream output = socket.getOutputStream(); // Send buffer
            InputStream input = socket.getInputStream();    // Receive buffer

            // Recibir mensaje
            String message = SocketUtils.receiveString(input);
            System.out.printf("Mensaje recibido del servidor: %s\n", message);

            // Enviar mensaje
            SocketUtils.sendString(output, "Client first message");

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