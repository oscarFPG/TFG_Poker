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

    public static void main(String[] args) {
        
        launch();

        Game game = new Game();
        Controller controller = new Controller(game);
        controller.run();
    }
}
