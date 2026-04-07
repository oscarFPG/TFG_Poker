package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddBotsCreateGameWindowController extends GenericController {
    
    @FXML
    private Button btnBackHome;

    @FXML
    private Button btnNextPlayers;

    @FXML
    private Button btnStartAddBots;

    @FXML
    private void initialize() {
        btnStartAddBots.setDisable(true);
    }

    @FXML
    public void jumpToHome(){
        back();
    }

    @FXML
    public void jumpToPlayers(){
        next();
    }

    @Override
    public void onNextEvent() {
    }

    @Override
    public void onBackEvent() {
        
    }

}