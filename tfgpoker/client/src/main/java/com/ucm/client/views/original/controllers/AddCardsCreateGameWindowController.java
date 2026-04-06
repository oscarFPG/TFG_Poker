package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.PokerGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddCardsCreateGameWindowController extends GenericController {
    
    @FXML
    private Button btnBackTable;

    @FXML
    private Button btnStartAddCards;

    @FXML
    private void initialize() {
        btnStartAddCards.setDisable(false);
    }
    
    @FXML
    private void sendClientInfo() {
        try {
            PokerGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println( String.format("Error server room name %s\n", e.getMessage()) );
        }
    }

    @FXML
    public void jumpToTable(){
        back();
    }

    @Override
    public void onNextEvent() {
       
    }

    @Override
    public void onBackEvent() {
        
    }
    
}
