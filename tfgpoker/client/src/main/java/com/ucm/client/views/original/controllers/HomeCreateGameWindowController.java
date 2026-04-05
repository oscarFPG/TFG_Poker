package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PokerGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class HomeCreateGameWindowController extends GenericController {

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnNextHome;

    @FXML
    private Button btnSaveHome;

    @FXML
    private TextField textFieldRoomName;

    @FXML
    private ComboBox<String> comboBlindsValue;

    @FXML
    private ComboBox<String> comboLevelDuration;

        @FXML
    private ComboBox<String> comboHikePercentage;

    @FXML
    private void initialize(){
        initializeBlindsValue();
        initializeLevelDuration();
        initializeHikePercentage();
    }

    private void initializeBlindsValue(){
        comboBlindsValue.getItems().add("1/2");
        int[] blinds = {5, 10, 25, 50, 100};
        String defaultValue = "DEFAULT";
        for (int blind : blinds){
            comboBlindsValue.getItems().add(blind + "/" + (blind * 2));
        }
        comboBlindsValue.getItems().add(defaultValue);
        comboBlindsValue.setValue(defaultValue);
    }

    private void initializeLevelDuration(){
        String defaultValue = "DEFAULT";
        comboLevelDuration.getItems().addAll(
            "15",
            "20",
            "30",
            "60",
            "120",
            "180"
        );
        comboLevelDuration.setValue(defaultValue);
    }

    private void initializeHikePercentage(){
        String defaultValue = "DEFAULT";
        comboHikePercentage.getItems().addAll(
            "25",
            "33",
            "50",
            "67",
            "80",
            "100"
        );
        comboHikePercentage.setValue(defaultValue);
    }

    @FXML
    public void returnChooseGame(){
        back();
    }

    @FXML
    public void jumpToBots(){
        next();
    }

    @FXML
    public void saveHomeConfig() {

        String roomName = textFieldRoomName.getText();


        if(GameConfig.isValidRoomName(roomName)) {
            _clientInfo.gameConfig._roomName = roomName;
            try {
                PokerGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());
            }
            catch (IOException e) {
                System.out.println( String.format("Error server room name %s\n", e.getMessage()) );
            }
        }
        else {
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