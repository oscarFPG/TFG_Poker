package com.ucm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/views/templateStyle.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/styles/templateStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}