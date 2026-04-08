package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddPlayersCreateGameWindowController extends GenericController  {

    @FXML
    private Button btnBackBots;

    @FXML
    private Button btnNextTable;

    @FXML
    private Button btnStartAddPlayers;


    @Override
    protected void onViewShown() {
        btnStartAddPlayers.setDisable(true);
    }

    @FXML
    public void jumpToBots(){
        back();
    }

    @FXML
    public void jumpToTable(){
        next();
    }

    @Override
    public void onNextEvent() {
      
    }

    @Override
    public void onBackEvent() {
       
    }

    
}
