package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PokerGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private Spinner<Integer> spinnerInitialMoney;

    @FXML
    private CheckBox checkBoxAllowBots;

    @FXML
    private ComboBox<String> comboBlindsValue;

    @FXML
    private ComboBox<String> comboLevelDuration;

    @FXML
    private ComboBox<String> comboHikePercentage;

    @Override
    protected void onViewShown() {
        btnStartHome.setDisable(true);
        initializeRoomName();
        labelUserName.setText(_clientInfo.name);
        checkBoxAllowBots.setSelected(_clientInfo.gameConfig._allowBots);
        initializeSpinner();
        initializeBlindsValue();
        initializeLevelDuration();
        initializeHikePercentage();
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
        comboBlindsValue.getItems().clear();
        int[] blinds = {1, 5, 10, 25, 50, 100};
        for (int blind : blinds){
            comboBlindsValue.getItems().add(blind + "/" + (blind * 2));
        }
        String restoreBlindsValue = _clientInfo.gameConfig._blindsValue;
        if( restoreBlindsValue != null) {
            comboBlindsValue.setValue(restoreBlindsValue);
        }
    }

    private void initializeLevelDuration() {
        comboLevelDuration.getItems().addAll(
            "15",
            "20",
            "30",
            "60",
            "120",
            "180"
        );
        String restoreLevelDuration = _clientInfo.gameConfig._levelDuration;
        if( restoreLevelDuration != null) {
            comboLevelDuration.setValue(restoreLevelDuration);
        }
    }

    private void initializeHikePercentage() {
        comboHikePercentage.getItems().addAll(
            "25",
            "33",
            "50",
            "67",
            "80",
            "100"
        );
        String restorehikePercentage = _clientInfo.gameConfig._hikePercentage;
        if( restorehikePercentage != null) {
            comboHikePercentage.setValue(restorehikePercentage);
        }
    }

    private void initializeSpinner() {
        int initialValue = _clientInfo.gameConfig._initialMoney;
        if(initialValue < MIN_INITIAL_MONEY || initialValue > MAX_INITIAL_MONEY) {
            initialValue = MIN_INITIAL_MONEY;
        }
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_INITIAL_MONEY, MAX_INITIAL_MONEY, initialValue);
        spinnerInitialMoney.setValueFactory(valueFactory);
        spinnerInitialMoney.setEditable(true);
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), initialValue, change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
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
        Integer initialMoney = spinnerInitialMoney.getValue();
        boolean allowBots = checkBoxAllowBots.isSelected();
        String blindsValue = comboBlindsValue.getValue();
        String levelDuration = comboLevelDuration.getValue();
        String hikePercentage = comboHikePercentage.getValue();

        boolean validRoomName = GameConfig.isValidRoomName(roomName);
        if(roomName == null || !validRoomName) {
            roomName = _clientInfo.name + "'s room";
        }
        if (blindsValue == null || blindsValue.isEmpty()) {
            blindsValue = _clientInfo.gameConfig._blindsValue;
        }
        if (levelDuration == null || levelDuration.isEmpty()) {
            levelDuration = _clientInfo.gameConfig._levelDuration;
        }
        if (hikePercentage == null || hikePercentage.isEmpty()) {
            hikePercentage = _clientInfo.gameConfig._hikePercentage;
        }
        _clientInfo.gameConfig._roomName = roomName;
        _clientInfo.gameConfig._userName = userName;
        _clientInfo.gameConfig._initialMoney = initialMoney;
        _clientInfo.gameConfig._allowBots = allowBots;
        _clientInfo.gameConfig._blindsValue = blindsValue;
        _clientInfo.gameConfig._levelDuration = levelDuration;
        _clientInfo.gameConfig._hikePercentage = hikePercentage;
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