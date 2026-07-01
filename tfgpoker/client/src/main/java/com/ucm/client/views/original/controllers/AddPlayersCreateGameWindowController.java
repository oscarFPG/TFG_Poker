package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class AddPlayersCreateGameWindowController extends GenericController  {

    private static final int MIN_NUM_PLAYERS = 0;
    private static final int MAX_NUM_PLAYERS = 8;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryPlayer;

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnBackBots;

    @FXML
    private Button btnNextTable;

    @FXML
    private Button btnStartAddPlayers;

    @FXML
    private Label labelRemainingPlayers;

    @FXML
    private Spinner<Integer> spinnerAddPlayers;
    
    @FXML
    private ComboBox<String> comboTurnTimerPlayer;

    @Override
    protected void onViewShown() {
        btnStartAddPlayers.setDisable(true);
        initializeSpinnner();
        initializeRemainingPlayers();
        initializeTurnTimerPlayer();
    }

    private void initializeSpinnner() {
        int maxPlayers = getMaxRemainingPlayers();
        int initialNumPlayers = _clientInfo.gameConfig._numPlayers;
        if(initialNumPlayers > maxPlayers) {
            initialNumPlayers = maxPlayers;
        }
        valueFactoryPlayer = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_PLAYERS, maxPlayers, initialNumPlayers);
        spinnerAddPlayers.setValueFactory(valueFactoryPlayer);
        spinnerAddPlayers.valueProperty().addListener((obs, oldValue, newValue) -> initializeRemainingPlayers());
    }

    private void initializeRemainingPlayers() {
        int maxPlayers = getMaxRemainingPlayers();
        int numPlayers = spinnerAddPlayers.getValue();
        int remainingPlayers = maxPlayers - numPlayers;
        labelRemainingPlayers.setText(String.valueOf(remainingPlayers));
    }

    private void initializeTurnTimerPlayer() {
        comboTurnTimerPlayer.getItems().addAll(
            "60",
            "180",
            "360",
            "720"
        );
        String restoreTurnTimerPlayer = _clientInfo.gameConfig._turnTimerPlayer;
        if(restoreTurnTimerPlayer != null && !comboTurnTimerPlayer.isDisable()) {
            comboTurnTimerPlayer.setValue(restoreTurnTimerPlayer);
        }
    }

    private int getMaxRemainingPlayers() {
        int totalBots = _clientInfo.gameConfig._numBots1 + _clientInfo.gameConfig._numBots2;
        return MAX_NUM_PLAYERS - totalBots;
    }

    private void saveAddPlayers() {
        _clientInfo.gameConfig._numPlayers = spinnerAddPlayers.getValue();
        String timerTurn = comboTurnTimerPlayer.getValue();

        if(timerTurn == null || timerTurn.isEmpty()) {
            timerTurn = _clientInfo.gameConfig._turnTimerPlayer;
        }

        _clientInfo.gameConfig._turnTimerPlayer = timerTurn;
    }

    @FXML
    public void returnChooseGame() {
        backWindow();
    }

    @FXML
    public void jumpToBots(){
        back();
    }

    @FXML
    public void jumpToTable(){
        next();
    }

    @Override
    public void onNextEvent() {
      saveAddPlayers();
    }

    @Override
    public void onBackEvent() {
       saveAddPlayers();
    }

}
