package com.ucm;


// Poker Game
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.ucm.control.Controller;
import com.ucm.evaluator.Evaluator;
import com.ucm.logic.Game;


public class ServerMain {

    private static int port = 5005;

    /*
     *  Desde la ruta TFGPOKER/tfgpoker
     * .\mvnw.cmd clean install   
     *  Run:
     *      .\mvnw.cmd -pl server -Prun exec:java
     *  Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * 
     * Run the Tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) {

        try{

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.printf("Socket servidor creado en el puerto %d\n", port);

            System.out.printf("Socket esperando conexion cliente...\n");
            Socket clientSocket = serverSocket.accept();
            System.out.printf("Conexion cliente aceptada!\n");

            OutputStream output = clientSocket.getOutputStream();   // Send Buffer
            InputStream input =  clientSocket.getInputStream();     // Receive buffer

            // Enviar mensaje
            SocketUtils.sendString(output, "Server first message");

            // Recibir mensaje
            String message = SocketUtils.receiveString(input);
            System.out.printf("Mensaje recibido del cliente: %s\n", message);

            serverSocket.close();
            clientSocket.close();
            return;
        }
        catch(IOException e){
            System.out.printf("ERROR: %s\n", e.getMessage());
        }
        

        try {
            Evaluator ev = Evaluator.getInstance();
            Game game = new Game();
            Controller controller = new Controller(game);
            controller.run();
        }
        catch (IOException e) {
            System.out.printf("Evaluator class failed on instaciating\n");
        }

    }
}