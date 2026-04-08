package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddCardsCreateGameWindowController extends GenericController {
    
    @FXML
    private Button btnBackTable;

    @FXML
    private Button btnStartAddCards;

    @FXML
    private void initialize() {
        btnStartAddCards.setDisable(false);
    }
    
    @FXML
    private void sendClientInfo() {

        try {
            PokerPreGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());

            int response = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
            if(response == GameType.ERROR_GAME_NOT_CREATED) {
                System.out.printf("Server response: Error creating game!\n");
            }
            else if(response == GameType.CONFIRMATION_WAITING_GAME) {
                System.out.printf("Server response: All correct! Creating room...\n");

                int clientType = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
                if(clientType == GameType.CONFIRMATION_HOST_PLAYER) {
                    _clientInfo.isHost = true;
                }
                else {
                    System.out.printf("Server response: clientType unknown %d\n", clientType);
                }

                next();
            }
        }
        catch (IOException e) {
            System.out.println( String.format("Error server room name: %s\n", e.getMessage()) );
        }
        catch(NullPointerException e) {
            System.out.println( String.format("Minor problem with socket ONLY for development: %s\n", e.getMessage()) );
        }
    }

    @FXML
    public void jumpToTable(){
        back();
    }

    @Override
    public void onNextEvent() {
       
    }

    @Override
    public void onBackEvent() {
        
    }
    
}
