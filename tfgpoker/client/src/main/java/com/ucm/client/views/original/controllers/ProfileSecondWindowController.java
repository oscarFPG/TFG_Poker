package com.ucm.client.views.original.controllers;

import com.ucm.client.utils.Messages;
import com.ucm.client.utils.NotificationManager;
import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * Controller class for the profile second window. It handles user interactions related to profile information, including IP address and name input, as well as avatar display. 
 * It validates the entered information and communicates with the server to ensure the name is valid before proceeding to the next step in the application.
 */
public class ProfileSecondWindowController extends GenericController {
    
    /**
     * The TextField for the IP address input.
     */
    @FXML
    private TextField ipLabel;
    /**
     * The TextField for the name input.
     */
    @FXML
    private TextField nameLabel;
    /**
     * The Button for saving the profile information.
     */
    @FXML
    private Button btnSave;
    /**
     * The Button for canceling the profile editing and exiting the application.
     */
    @FXML
    private Button btnCancel;
    /**
     * The ImageView for displaying the user's avatar based on the entered name.
     */
    @FXML
    private ImageView imgAvatarProfile;

    /**
     * {@inheritDoc}
     */
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
    /**
     * Saves the profile information entered by the user, including the server IP address and name. It validates the IP address and sends the name to the server for validation. Based on the server's response, it displays appropriate notifications to the user. If the name is valid, it proceeds to the next step in the application.
     */
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
                NotificationManager.showError(Messages.Notifications.ERROR_NAME_TOO_SHORT);
            }
            else if(response == GameType.ERROR_NAME_TOO_LONG) {
                System.out.printf("Server response: Name is too long!\n");
                NotificationManager.showError(Messages.Notifications.ERROR_NAME_TOO_LONG);
            }
            else if (response == GameType.ERROR_NAME_ALREADY_USED) {
                 NotificationManager.showError(Messages.Notifications.ERROR_NAME_ALREADY_USED);
            }
            else if(response == GameType.CONFIRMATION_NAME_VALID) {
                System.out.printf("Server response: Name is valid!\n");
                NotificationManager.showSuccess(Messages.Notifications.CONFIRMATION_NAME_VALID + _clientInfo.name  + "!" );
                next();
            }
            else {
                System.out.printf("Unknown server response : %d\n", response);
                NotificationManager.showError( Messages.Notifications.UNKNOWN_SERVER_ERROR + response );
            }
        }
        catch(Exception e) {
            System.out.printf("Error connecting to the socket: %s\n", e.getMessage());
            NotificationManager.showError(Messages.Notifications.ERROR_CONNECTING_TO_SOCKET + e.getMessage());
        }
        
    }
    /**
     * Cancels the profile editing and exits the application.
     */
    @FXML
    private void cancel() {
        Platform.exit();
    }
}