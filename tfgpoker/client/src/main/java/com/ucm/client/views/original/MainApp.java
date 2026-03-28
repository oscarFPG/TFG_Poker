
package com.ucm.client.views.original;

import com.ucm.client.views.original.controllers.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    /*
    Run javafx: .\mvnw.cmd -pl client -Prun javafx:run
    */

    @Override
    public void start(Stage primaryStage) throws Exception {
       MainController mainController = new MainController(primaryStage);
       mainController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}