package com.ucm.client.views.original.controllers;

import java.io.IOException;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

public class AddBotsCreateGameWindowController extends GenericController {
    
    private static final int MIN_NUM_BOTS = 0;

    private static final int MAX_NUM_BOTS = 8;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryBot1;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryBot2;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryBot3;

    @FXML
    private Button btnBackChooseGame;

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
    private StackPane cardInfoBot1;

    @FXML
    private VBox cardFrontBot1;

    @FXML
    private StackPane cardBackBot1;

    private boolean isFlipped = false;

    @Override
    protected void onViewShown() {
        
        btnStartAddBots.setDisable(true);
        initializeSpinners();
        boolean allowBots = _clientInfo.gameConfig._allowBots;
        spinnerBot1.setDisable(!allowBots);
        spinnerBot2.setDisable(!allowBots);
        cardBackBot1.setRotate(180);
        if(!allowBots) {
            valueFactoryBot1.setValue(0);
            valueFactoryBot2.setValue(0);
        }
    }

    private void initializeSpinners() {
        valueFactoryBot1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, MAX_NUM_BOTS, _clientInfo.gameConfig._numBots1);
        valueFactoryBot2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, MAX_NUM_BOTS, _clientInfo.gameConfig._numBots2);
        spinnerBot1.setValueFactory(valueFactoryBot1);
        spinnerBot2.setValueFactory(valueFactoryBot2);
        spinnerBot1.valueProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged());
        spinnerBot2.valueProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged());
        updateSpinners();
    }

    private void onSpinnerChanged() {
        updateSpinners();
    }

    private void updateSpinners() {
        int bot1 = spinnerBot1.getValue();
        int bot2 = spinnerBot2.getValue();
        int totalBots = bot1 + bot2;
        int remainingBots = MAX_NUM_BOTS - totalBots;
        valueFactoryBot1.setMax(bot1 + remainingBots);
        valueFactoryBot2.setMax(bot2 + remainingBots);
    }

    @FXML
    private void flipCardBot1() {

        if (!isFlipped) {

            RotateTransition first = new RotateTransition(Duration.millis(200), cardFrontBot1);
            first.setFromAngle(0);
            first.setToAngle(90);
            first.setAxis(Rotate.Y_AXIS);

            RotateTransition second = new RotateTransition(Duration.millis(200), cardBackBot1);
            second.setFromAngle(-90);
            second.setToAngle(0);
            second.setAxis(Rotate.Y_AXIS);

            first.setOnFinished(e -> {
                cardFrontBot1.setVisible(false);
                cardBackBot1.setVisible(true);
                second.play();
            });

            first.play();

        } else {

            RotateTransition first = new RotateTransition(Duration.millis(200), cardBackBot1);
            first.setFromAngle(0);
            first.setToAngle(90);
            first.setAxis(Rotate.Y_AXIS);

            RotateTransition second = new RotateTransition(Duration.millis(200), cardFrontBot1);
            second.setFromAngle(-90);
            second.setToAngle(0);
            second.setAxis(Rotate.Y_AXIS);

            first.setOnFinished(e -> {
                cardBackBot1.setVisible(false);
                cardFrontBot1.setVisible(true);
                second.play();
            });

            first.play();
        }

        isFlipped = !isFlipped;
    }

    @FXML
    public void returnChooseGame() {
        backWindow();
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