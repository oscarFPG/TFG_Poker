package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseGameWindowController extends GenericController {
    
    @FXML
    private Button btnCreateGame;


    @FXML
    private void onCreateGame() {
        mainController.chooseCreateGame();
    }

}
