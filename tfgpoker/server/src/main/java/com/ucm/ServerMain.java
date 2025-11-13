package com.ucm;


// Poker Game
import com.ucm.control.Controller;
import com.ucm.evaluator.Evaluator;
import com.ucm.logic.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerMain {

    private static int port = 5005;

    /*
     *  Desde la ruta TFGPOKER/tfgpoker
     *  Run:
     *      .\mvnw.cmd -pl server -Prun exec:java
     *  Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     */
    public static void main(String[] args) {

        try{

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.printf("Socket creado en el puerto %d\n", port);

            System.out.printf("Socket esperando conexion cliente...\n");
            Socket clientSocket = serverSocket.accept();
            System.out.printf("Socket creado con exito\n");
        }
        catch(IOException e){
            System.out.printf("Error al crear y establecer la conexion con el socket\n");
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