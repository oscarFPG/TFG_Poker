
package com.ucm.client;

import com.ucm.client.utils.DialogsManager;
import com.ucm.client.views.original.controllers.MainController;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class of the application, responsible for initializing the JavaFX application and setting up the main window.
 */
public class MainApp extends Application {
    /*
        Run javafx: .\mvnw.cmd -pl client javafx:run
    */

    /**
     * Path to the application icon image used in the main window.
     */
    private static final String APP_ICON_PATH = "/images/logoPoker.png";

   /**
    * Starts the JavaFX application by setting up the primary stage, including the title and icon, and initializing the main controller.
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
    /**
     * The main entry point of the application, which launches the JavaFX application.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}