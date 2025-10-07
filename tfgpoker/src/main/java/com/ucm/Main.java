package com.ucm;

//GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// LOGIC
import com.ucm.control.Controller;
import com.ucm.logic.Game;


public class Main extends Application {

    @Override
    public void start(Stage stage) {

        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }


    /*
     * Debug: 
     *  Primero: 
     *      Ejecutar el comando:    cd tfgpoker ; .\mvnwDebug.cmd clean compile exec:java
     *      Debe aparecer el mensaje: "Listening for transport dt_socket at address: 8000"
     *  Segundo: 
     *      cd tfgpoker ; .\mvnw.cmd clean
     *      Darle a la tarea "Debug JavaFX project" en el apartado "Run and Debug" de VSCode
     */
    public static void main(String[] args) {
        
        launch();

        Game game = new Game();
        Controller controller = new Controller(game);
        controller.run();
    }
}