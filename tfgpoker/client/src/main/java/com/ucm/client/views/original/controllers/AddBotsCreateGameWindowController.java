package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class AddBotsCreateGameWindowController extends GenericController {
    
    private static final int MIN_NUM_BOTS = 0;

    private static final int MAX_NUM_BOTS = 8;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryBot1;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryBot2;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryBot3;

    @FXML
    private Button btnBackHome;

    @FXML
    private Button btnNextPlayers;

    @FXML
    private Button btnStartAddBots;

    @FXML
    private Spinner<Integer> spinnerBot1;

    @FXML
    private Spinner<Integer> spinnerBot2;

        @FXML
    private Spinner<Integer> spinnerBot3;

    @Override
    protected void onViewShown() {
        btnStartAddBots.setDisable(true);
        initializeSpinners();
        
    }

    private void initializeSpinners() {
 
        valueFactoryBot1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, MAX_NUM_BOTS, _clientInfo.gameConfig._numBots1);
        valueFactoryBot2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, MAX_NUM_BOTS, _clientInfo.gameConfig._numBots2);
        valueFactoryBot3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, MAX_NUM_BOTS, _clientInfo.gameConfig._numBots3);
        spinnerBot1.setValueFactory(valueFactoryBot1);
        spinnerBot2.setValueFactory(valueFactoryBot2);
        spinnerBot3.setValueFactory(valueFactoryBot3);
        spinnerBot1.valueProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged());
        spinnerBot2.valueProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged());
        spinnerBot3.valueProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged());
        updateSpinners();
    }

    private void onSpinnerChanged() {
        updateSpinners();
    }

    private void updateSpinners() {
        int bot1 = spinnerBot1.getValue();
        int bot2 = spinnerBot2.getValue();
        int bot3 = spinnerBot3.getValue();
        int totalBots = bot1 + bot2 + bot3;
        int remainingBots = MAX_NUM_BOTS - totalBots;
        valueFactoryBot1.setMax(bot1 + remainingBots);
        valueFactoryBot2.setMax(bot2 + remainingBots);
        valueFactoryBot3.setMax(bot3 + remainingBots);
    }

    @FXML
    public void jumpToHome(){
        back();
    }

    @FXML
    public void jumpToPlayers(){
        next();
    }

    private void saveAddBotsConfig() {
        _clientInfo.gameConfig._numBots1 = spinnerBot1.getValue();
        _clientInfo.gameConfig._numBots2 = spinnerBot2.getValue();
        _clientInfo.gameConfig._numBots3 = spinnerBot3.getValue();
    }

    @Override
    public void onNextEvent() {
        saveAddBotsConfig();
    }

    @Override
    public void onBackEvent() {
        saveAddBotsConfig();
    }
}