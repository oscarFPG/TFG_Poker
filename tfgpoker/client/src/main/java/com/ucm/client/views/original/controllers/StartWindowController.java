package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller class for the start window of the application. It handles the user interaction with the start button and initiates the transition to the next window when the button is pressed.
 */
public class StartWindowController extends GenericController {
    
    /**
     * The button that starts the application.
     */
    @FXML
    private Button startButton;
    /**
     * The method that is called when the start button is pressed. It calls the next() method to proceed to the next window.
     */
    @FXML
    private void startApp() {
        next();
    }
}
