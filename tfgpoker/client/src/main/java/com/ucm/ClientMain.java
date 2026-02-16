package com.ucm;


import java.util.Scanner;

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
     * .\mvnw.cmd clean install
     * Run:
     * .\mvnw.cmd -pl client -Prun exec:java
     * Debug:
     * .\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     * .\mvnw.cmd test
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.printf("Introduzca un nombre: ");
        name = scanner.next();
        System.out.printf("Bienvenido %s\n", name);
       
        scanner.close();
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Llamar a este metodo con: launch(args);
        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }

}