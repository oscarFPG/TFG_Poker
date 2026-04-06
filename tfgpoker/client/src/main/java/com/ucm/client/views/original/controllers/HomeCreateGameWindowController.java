package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PokerGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class HomeCreateGameWindowController extends GenericController {

    private static final int MAX_ROOM_NAME_LENGTH = 17;
    private static final int MIN_INITIAL_MONEY = 100;
    private static final int MAX_INITIAL_MONEY = 1000000;

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnNextHome;

    @FXML
    private Button btnStartHome;

    @FXML
    private TextField textFieldRoomName;

    @FXML
    private Label labelUserName;

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
        btnStartHome.setDisable(true);
        initializeBlindsValue();
        initializeLevelDuration();
        initializeHikePercentage();
        initializeSpinner();
    }

    @Override
    protected void onViewShown() {

        initializeRoomName();
        labelUserName.setText(_clientInfo.name);
    }

    private void initializeRoomName() {
        String roomName = _clientInfo.gameConfig._roomName;
        if(!GameConfig.isValidRoomName(roomName)) {
            roomName = _clientInfo.name + "'s room";
            _clientInfo.gameConfig._roomName = roomName;
        }
        textFieldRoomName.setText(roomName);
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
        comboBlindsValue.setValue(defaultValue);
        for (int blind : blinds){
            comboBlindsValue.getItems().add(blind + "/" + (blind * 2));
        }
        comboBlindsValue.getItems().add(defaultValue);
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
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_INITIAL_MONEY, MAX_INITIAL_MONEY, MIN_INITIAL_MONEY);
        spinnerInitialMoney.setValueFactory(valueFactory);
        spinnerInitialMoney.setEditable(true);
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), MIN_INITIAL_MONEY, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
        spinnerInitialMoney.getEditor().setTextFormatter(formatter);
        formatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            int clampedValue = Math.clamp(newValue, MIN_INITIAL_MONEY, MAX_INITIAL_MONEY);
            valueFactory.setValue(clampedValue);
        });
    }

    @FXML
    public void returnChooseGame() {
        back();
    }

    @FXML
    public void jumpToBots() {
        next();
    }

    private void saveHomeConfig() {
        
        String roomName = textFieldRoomName.getText();
        String userName = labelUserName.getText();
        boolean allowBots = false;  // ESTO TIENE QUE LEERSE DE LA INTERFAZ !!!

        boolean validRoomName = GameConfig.isValidRoomName(roomName);
        if(roomName == null || !validRoomName) {
            roomName = _clientInfo.name + "'s room";
        }

        _clientInfo.gameConfig._roomName = roomName;
        _clientInfo.gameConfig._userName = userName;
        _clientInfo.gameConfig._allowBots = allowBots;
    }

    @Override
    public void onNextEvent() {
        saveHomeConfig();
    }

    @Override
    public void onBackEvent() {
        saveHomeConfig();
    }

}