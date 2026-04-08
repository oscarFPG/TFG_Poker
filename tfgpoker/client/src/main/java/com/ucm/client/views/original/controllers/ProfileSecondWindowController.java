package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.net.Socket;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;



public class ProfileSecondWindowController extends GenericController {

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

        boolean validIP = PokerPreGame.checkIpValid(serverIP);
        if(!validIP) {
            serverIP = PokerPreGame.LOCAL_HOST;
        }

        _clientInfo.ip = serverIP;
        _clientInfo.name = name;

        try {
            _clientInfo.socket = PokerPreGame.connect(serverIP);
            PokerPreGame.sendName(_clientInfo.name, _clientInfo.socket);
            
            int response = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
            if(response == GameType.ERROR_NAME_TOO_SHORT) {
                System.out.printf("Server response: Name is too short!\n");
            }
            else if(response == GameType.ERROR_NAME_TOO_LONG) {
                System.out.printf("Server response: Name is too long!\n");
            }
            else if(response == GameType.CONFIRMATION_NAME_VALID) {
                System.out.printf("Server response: Name is valid!\n");

                next();
            }
            else {
                System.out.printf("Unknown server response : %d\n", response);
            }
        }
        catch(Exception e) {
            System.out.printf("Error connecting to the socket: %s\n", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        Platform.exit();
    }

    @Override
    public void onNextEvent() {}

    @Override
    public void onBackEvent() {}
}