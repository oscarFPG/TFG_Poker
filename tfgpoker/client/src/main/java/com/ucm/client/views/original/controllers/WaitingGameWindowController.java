package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class WaitingGameWindowController extends GenericController {

    @FXML
    private Label playerName0;
    @FXML
    private Label playerMoney0;

    @FXML
    private Label playerName1;
    @FXML
    private Label playerMoney1;

    @FXML
    private Label playerName2;
    @FXML
    private Label playerMoney2;

    @FXML
    private Label playerName3;
    @FXML
    private Label playerMoney3;

    @FXML
    private Label playerName4;
    @FXML
    private Label playerMoney4;

    @FXML
    private Label playerName5;
    @FXML
    private Label playerMoney5;

    @FXML
    private Label playerName6;
    @FXML
    private Label playerMoney6;

    @FXML
    private Label playerName7;
    @FXML
    private Label playerMoney7;

    @FXML
    private Label playerName8;
    @FXML
    private Label playerMoney8;


    @FXML
    public void initialize() {

        _clientInfo = ClientInfo.getInstance();

        playerName0.setText( _clientInfo.name );
        playerMoney0.setText( String.valueOf(1000) );

        waitForPlayers();
        System.out.printf("Saliendo de estar esperando...\n");
    }

    private void waitForPlayers() {

        try {

            Thread waitThread = new Thread(() -> {

            try {
                System.out.printf("Waiting...\n");
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.printf("Error: %s\n", e.getMessage());
            }
        });
            waitThread.start();
            waitThread.join();
        }
        catch (InterruptedException e) {
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