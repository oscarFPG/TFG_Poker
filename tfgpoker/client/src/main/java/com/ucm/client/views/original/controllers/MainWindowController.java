package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainWindowController extends GenericController {
    
    @FXML
    private Button btnSaveMain;

    @FXML
    private void chooseGame() {
        mainController.next();
    }

}
