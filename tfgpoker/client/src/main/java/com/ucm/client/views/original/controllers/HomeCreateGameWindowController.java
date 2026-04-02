package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PokerGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HomeCreateGameWindowController extends GenericController {

    @FXML
    private Button btnBackChooseGame;
    @FXML
    private Button btnNextHome;
    @FXML
    private TextField textFieldRoomName;

    @FXML
    public void returnChooseGame(){
        back();
    }

    @FXML
    public void jumpToBots(){
        next();
    }

    @FXML
    public void roomName(){
        String roomName = textFieldRoomName.getText();
        if(GameConfig.isValidRoomName(roomName)){
            _clientInfo.gameConfig._roomName = roomName;
            try {
                PokerGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Error server room name");
            }
        }
        else{
            if(GameConfig.checkRoomName(roomName) == GameType.ERROR_NAME_TOO_SHORT){
                System.out.println("Name too short, try again");
            }
            else if (GameConfig.checkRoomName(roomName) == GameType.ERROR_NAME_TOO_LONG){
                System.out.println("Name too long, try again");
            }
        }
        
    }

    @Override
    public void onNextEvent() {
    }

    @Override
    public void onBackEvent() {
    }

}