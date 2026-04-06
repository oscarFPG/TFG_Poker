package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


public class WaitingGameWindowController extends GenericController {

    @FXML
    private Label playerNamePlaceholder;

    @FXML
    private StackPane pokerPlayer0;

    @FXML
    private StackPane pokerPlayer1;

    @FXML
    private StackPane pokerPlayer2;

    @FXML
    private StackPane pokerPlayer3;

    @FXML
    private StackPane pokerPlayer4;

    @FXML
    private StackPane pokerPlayer5;

    @FXML
    private StackPane pokerPlayer6;

    @FXML
    private StackPane pokerPlayer7;

    @FXML
    private StackPane pokerPlayer8;


    @FXML
    public void initialize() {

        _clientInfo = ClientInfo.getInstance();
        playerNamePlaceholder.setText( _clientInfo.name );
    
        updatePlayerInfo(pokerPlayer0, _clientInfo.name);
    }


    private void updatePlayerInfo(StackPane playerStackPane, String name) {


    }

    @Override
    public void onNextEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onNextEvent'");
    }


    @Override
    public void onBackEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onBackEvent'");
    }



}