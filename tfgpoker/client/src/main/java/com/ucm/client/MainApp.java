
package com.ucm.client;

import com.ucm.client.utils.DialogsManager;
import com.ucm.client.views.original.controllers.MainController;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    
    /*
        Run javafx: .\mvnw.cmd -pl client javafx:run
    */
    @Override
    public void start(Stage primaryStage) throws Exception {
       MainController mainController = new MainController(primaryStage);
       DialogsManager.init(primaryStage);
       mainController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}