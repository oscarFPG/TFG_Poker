package com.ucm.server;


import java.io.IOException;
import com.ucm.server.control.Controller;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.logic.Game;


public class ServerMain {

    /*
     * Debug:
     * Primero:
     * Ejecutar el comando: cd tfgpoker ; .\mvnwDebug.cmd exec:java
     * Debe aparecer el mensaje:
     * "Listening for transport dt_socket at address: 8000"
     * Segundo:
     * Darle a la tarea "Debug Poker project" en el apartado "Run and Debug" de
     * VSCode
     * 
     * 
     * Run:
     * .\mvnw.cmd exec:java
     */
    public static void main(String[] args) {

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