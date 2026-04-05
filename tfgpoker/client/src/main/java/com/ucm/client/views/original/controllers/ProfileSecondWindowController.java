package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.net.Socket;

import com.ucm.client.ClientInfo;
import com.ucm.common.PokerGame;
import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;



public class ProfileSecondWindowController extends GenericController {


    public class Config {

        public boolean alloBots;
    }

    @FXML
    private TextField ipLabel;

    @FXML
    private TextField nameLabel;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;


    @FXML
    public void initialize() {

        btnSave.setDisable(true);
        nameLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.trim().isEmpty()) {
                btnSave.setDisable(true);
            } 
            else {
                btnSave.setDisable(false);
            }
        });
    }


    @FXML
    private void save() {
        
        String serverIP = ipLabel.getText();
        String name = nameLabel.getText();

        boolean validIP = PokerGame.checkIpValid(serverIP);
        if(!validIP) {
            serverIP = PokerGame.LOCAL_HOST;
        }

        _clientInfo = ClientInfo.getInstance();
        _clientInfo.ip = serverIP;
        _clientInfo.name = name;

        try {
            _clientInfo.socket = PokerGame.connect(serverIP);
            PokerGame.sendName(_clientInfo.name, _clientInfo.socket);
            //acordarse de recibir estado del servidor y pasar a la siguiente interfaz, solo, si todo va bien
        }
        catch(IOException e) {
            System.out.printf("Error connecting to the socket: %s", e.getMessage());
        }
        
        next();
    }


    @FXML
    private void cancel() {
        Platform.exit();
    }


    @Override
    public void onNextEvent() {


    }


    @Override
    public void onBackEvent() {
   
        
    }
}
