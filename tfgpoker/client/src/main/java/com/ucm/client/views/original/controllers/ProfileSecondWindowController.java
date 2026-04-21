package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.net.Socket;

import com.ucm.client.AvatarGenerator;
import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;



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
    private ImageView imgAvatarProfile;


    @Override
    protected void onViewShown() {

        btnSave.setDisable(true);
        nameLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.trim().isEmpty()) {
                btnSave.setDisable(true);
            } 
            else {
                btnSave.setDisable(false);
                _clientInfo.onNameChanged(newValue.trim());
                imgAvatarProfile.setImage(_clientInfo.getAvatar(_clientInfo.name,140));
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
            else if (response == GameType.ERROR_NAME_ALREADY_USED) {
                showNameAlreadyUsedAlert();
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

    private void showNameAlreadyUsedAlert() {
        Alert alert = new Alert (Alert.AlertType.WARNING);
        alert.setTitle("Name cannot be registrated");
        alert.setHeaderText("Name already used");
        alert.setContentText("You must add a name that has not already register");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/original/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");
        alert.showAndWait();
    }

    @FXML
    private void cancel() {
        Platform.exit();
    }
}