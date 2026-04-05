package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PokerGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class HomeCreateGameWindowController extends GenericController {

    private static final int MAX_ROOM_NAME_LENGTH = 17;
    private static final int MIN_INITIAL_MONEY = 100;
    private static final int MAX_INITIAL_MONEY = 1000000;

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
    private Spinner<Integer> spinnerInitialMoney;

    @FXML
    private void initialize() {
    
        _clientInfo = ClientInfo.getInstance();
        initializeRoomName();
        initializeBlindsValue();
        initializeLevelDuration();
        initializeHikePercentage();
        initializeSpinner();
    }

    private void initializeRoomName(){
        textFieldRoomName.setText(_clientInfo.name + "'s room");
        textFieldRoomName.selectAll();
        textFieldRoomName.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > MAX_ROOM_NAME_LENGTH) {
                return null;
            }
            if (!newText.matches("[a-zA-Z0-9 ]*")) {
                return null;
            }
            return change;
        }));
    }

    private void initializeBlindsValue() {

        int[] blinds = {1, 5, 10, 25, 50, 100};
        String defaultValue = "DEFAULT";

        comboBlindsValue.getItems().add(defaultValue);
        comboBlindsValue.setValue(defaultValue);
        for (int blind : blinds){
            comboBlindsValue.getItems().add(blind + "/" + (blind * 2));
        }
    }

    private void initializeLevelDuration() {
        
        String defaultValue = "DEFAULT";
        comboLevelDuration.getItems().addAll(
            "15",
            "20",
            "30",
            "60",
            "120",
            "180"
        );
        comboLevelDuration.getItems().add(defaultValue);
        comboLevelDuration.setValue(defaultValue);
    }

    private void initializeHikePercentage() {

        String defaultValue = "DEFAULT";
        comboHikePercentage.getItems().addAll(
            "25",
            "33",
            "50",
            "67",
            "80",
            "100"
        );
        comboHikePercentage.getItems().add(defaultValue);
        comboHikePercentage.setValue(defaultValue);
    }

    private void initializeSpinner() {
        
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 100000000, 100);
        TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            
            if (Integer.parseInt(newText) < MIN_INITIAL_MONEY || Integer.parseInt(newText) > MAX_INITIAL_MONEY) {
                return null;
            }
            if (!newText.matches("\\d*")) {
                return null;
            }
            return change;
        });
        spinnerInitialMoney.getEditor().setTextFormatter(formatter);
        spinnerInitialMoney.setValueFactory(valueFactory);
        spinnerInitialMoney.setEditable(true);
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
        boolean allowBots = false;  // ESTO TIENE QUE LEERSE DE LA INTERFAZ !!!

        boolean validRoomName = GameConfig.isValidRoomName(roomName);
        if(roomName == null || !validRoomName) {
            roomName = _clientInfo.name + "'s room";
        }

        _clientInfo.gameConfig._roomName = roomName;
        _clientInfo.gameConfig._allowBots = allowBots;

        try {
            PokerGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println( String.format("Error server room name %s\n", e.getMessage()) );
        }
    }

    @Override
    public void onNextEvent() {
    }

    @Override
    public void onBackEvent() {
    }

}