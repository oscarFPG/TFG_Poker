
package com.ucm.client;

import com.ucm.client.utils.DialogsManager;
import com.ucm.client.views.original.controllers.MainController;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String APP_ICON_PATH = "/images/logoPoker.png";
    
    /*
        Run javafx: .\mvnw.cmd -pl client javafx:run
    */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Title and main window app icon
        primaryStage.setTitle("PokerTFG");
        primaryStage.getIcons().add(
            new Image(
                getClass().getResourceAsStream(APP_ICON_PATH)
            )
        );

        // Main controller setup and start
        MainController mainController = new MainController(primaryStage);
        DialogsManager.init(primaryStage);
        mainController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}