package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseGameWindowController extends GenericController {
    
    @FXML
    private Button btnCreateGame;


    @FXML
    private void onCreateGame() {
        
        try {
            SocketUtils.sendInteger(_clientInfo.socket.getOutputStream(), GameType.PETITION_CREATE_GAME);
            System.out.printf("CREATE_GAME sent\n");

            int response = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
            if(response == GameType.ERROR_GAME_ALREADY_CREATED) {
                System.out.printf("Error response from server: %d\n", response);
                return;
            }
            else if(response == GameType.CONFIRMATION_CREATE_GAME) {
                System.out.printf("OK\n");
            }
        }
        catch (IOException e) {
           System.out.printf("Error sending petition to create game: %s", e.getMessage());
        }

       _mainController.chooseCreateGame();
        
    }


    @Override
    public void onNextEvent() {
    }


    @Override
    public void onBackEvent() {
    }

}
