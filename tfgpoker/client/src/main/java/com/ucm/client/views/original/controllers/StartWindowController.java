package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientMain;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StartWindowController extends GenericController {
    @FXML
    private Button startButton;

    public void initialize() {
        startButton.setOnAction(e -> {
            mainController.next();
        });
    }
}
