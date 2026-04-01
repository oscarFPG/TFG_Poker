package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddTableCreateGameWindowController extends GenericController {

    @FXML
    private Button btnBackPlayers;
    @FXML
    private Button btnNextCards;

    @FXML
    public void jumpToPlayers(){
        back();
    }

    @FXML
    public void jumpToCards(){
        next();
    }

    @Override
    public void onNextEvent() {
        
    }

    @Override
    public void onBackEvent() {
        
    }
    
}
