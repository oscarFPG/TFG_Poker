package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class StartWindowController extends GenericController {
    
    @FXML
    private Button startButton;

    @FXML
    private void startApp() {
        next();
    }
}
