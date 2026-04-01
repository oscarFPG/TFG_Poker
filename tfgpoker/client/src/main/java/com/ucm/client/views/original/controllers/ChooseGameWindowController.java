package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseGameWindowController extends GenericController {
    
    @FXML
    private Button btnCreateGame;


    @FXML
    private void onCreateGame() {
        
       _mainController.chooseCreateGame();
    }


    @Override
    public void onNextEvent() {
    }


    @Override
    public void onBackEvent() {
    }

}
