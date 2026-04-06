package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.SocketUtils;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


public class WaitingGameWindowController extends GenericController {

    @FXML
    private Label playerName0, playerMoney0;

    @FXML
    private Label playerName1, playerMoney1;

    @FXML
    private Label playerName2, playerMoney2;

    @FXML
    private Label playerName3, playerMoney3;

    @FXML
    private Label playerName4, playerMoney4;

    @FXML
    private Label playerName5, playerMoney5;

    @FXML
    private Label playerName6, playerMoney6;

    @FXML
    private Label playerName7, playerMoney7;

    @FXML
    private Label playerName8, playerMoney8;


    @Override
    protected void onViewShown() {

        playerName0.setText( _clientInfo.name );
        playerMoney0.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );

        try {
            _clientInfo.id = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());

            System.out.printf("Player ID is %d\n", _clientInfo.id);
        }
        catch(Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }

    }


    @Override
    public void onNextEvent() {
        
    }


    @Override
    public void onBackEvent() {
       
    }



}