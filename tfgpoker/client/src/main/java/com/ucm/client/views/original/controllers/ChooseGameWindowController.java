package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ucm.client.utils.NotificationManager;
import com.ucm.client.utils.Messages;

import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * class that controls the ChooseGameWindow view
 */
public class ChooseGameWindowController extends GenericController {
    
    /**
     * FXML button to go back to the main window
     */
    @FXML
    private Button btnBackMainWindow;
    /**
     * FXML button to go to the next window corresponded to the create a new game window
     */
    @FXML
    private Button btnCreateGame;
    /**
     * FXML button to join a game
     */
    @FXML
    private Button btnJoinGame;
    /**
     * method that is called when the user clicks the create game button, it calls the chooseCreateGame method from the main controller
     */
    @FXML
    private void onCreateGame() {
        chooseCreateGame();
    }
    /**
     * method that is called when the user clicks the join game button, it sends a petition to the server to join a game and handles the response accordingly
     * if the response is a confirmation, it receives the game configuration and sets the client type
     */
    @FXML
    private void onJoinGame() {

        try {

            InputStream input = _clientInfo.socket.getInputStream();
            OutputStream output = _clientInfo.socket.getOutputStream();

            
            SocketUtils.sendInteger(output, GameType.PETITION_JOIN_GAME);
            int response = SocketUtils.receiveInt(input);
            if(response == GameType.CONFIRMATION_WAITING_GAME) {

                _clientInfo.gameConfig = PokerPreGame.receiveGameConfigAsJoinedPlayer(input, output);

                int clientType = SocketUtils.receiveInt(input);
                if(clientType == GameType.CONFIRMATION_NO_HOST_PLAYER) {
                    _clientInfo.isHost = false;
                }
                else {
                    System.out.printf("Server response: clientType unknown %d\n", clientType);
                    NotificationManager.showError(Messages.Notifications.ERROR_CLIENT_TYPE + clientType);
                }

                chooseJoinGame();
            }
            else if(response == GameType.ERROR_GAME_NOT_JOINED) {
                System.out.printf("Server response: Client cannot join!\n");
                NotificationManager.showError(Messages.Notifications.ERROR_GAME_NOT_JOINED);

            }

        }
        catch (IOException e) {
            System.out.printf("Error receiving response from server: %s\n", e.getMessage());   
            NotificationManager.showError(Messages.Notifications.ERROR_RECEIVING_RESPONSE_SERVER + e.getMessage());
        }

    }
    /**
     * method that is called when the user clicks the back button, it calls the back method from the generic controller
     */
    @FXML
    public void returnMainWindow() {
        back();
    }

}
