package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class StartWindowController extends GenericController {
    
    @FXML
    private Button startButton;

    @FXML
    private void startApp() {
        next();
    }
}
