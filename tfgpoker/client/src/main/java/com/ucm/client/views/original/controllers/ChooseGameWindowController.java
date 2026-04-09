package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseGameWindowController extends GenericController {
    
    @FXML
    private Button btnBackMainWindow;

    @FXML
    private Button btnCreateGame;

    @FXML
    private Button btnJoinGame;

    @FXML
    private void onCreateGame() {
        chooseCreateGame();
    }

    @FXML
    private void onJoinGame() {

        try {

            SocketUtils.sendInteger(_clientInfo.socket.getOutputStream(), GameType.PETITION_JOIN_GAME);

            int response = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
            if(response == GameType.CONFIRMATION_WAITING_GAME) {
                System.out.printf("Server response: Client joined succesfully!\n");

                int clientType = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
                if(clientType == GameType.CONFIRMATION_NO_HOST_PLAYER) {
                    _clientInfo.isHost = false;
                }
                else {
                    System.out.printf("Server response: clientType unknown %d\n", clientType);
                }

                chooseJoinGame();
            }
            else if(response == GameType.ERROR_GAME_NOT_JOINED) {
                System.out.printf("Server response: Client cannot join!\n");
            }

        }
        catch (IOException e) {
            System.out.printf("Error receiving response from server: %s\n", e.getMessage());    
        }
    }

    @FXML
    public void returnMainWindow() {
        back();
    }


    @Override
    public void onNextEvent() {
    }


    @Override
    public void onBackEvent() {
    }

}
