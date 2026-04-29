package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ucm.client.ClientInfo;
import com.ucm.common.BotDescriptor;
import com.ucm.common.BotRegistry;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private final List<BotCardController> botCards = new ArrayList<>();

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnBackHome;

    @FXML
    private Button btnNextPlayers;

    @FXML
    private Button btnStartAddBots;

    @FXML
    private javafx.scene.layout.HBox botsContainer;

    @Override
    protected void onViewShown() {
        
        btnStartAddBots.setDisable(true);

        botsContainer.getChildren().clear();
        botCards.clear();

        boolean allowBots = _clientInfo.gameConfig._allowBots;

        for(BotDescriptor bot: BotRegistry.getAvailableBots()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/botCard.fxml"));

                StackPane card = loader.load();
                BotCardController controller = loader.getController();
                controller.setup(bot, MAX_NUM_BOTS, allowBots);
                int previousValue= _clientInfo.gameConfig.getBotCount(bot.botId());
                controller.setInitialValue(previousValue);
                controller.setOnValueChanged(this::updateSpinners);

                botCards.add(controller);
                botsContainer.getChildren().add(card);
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            updateSpinners();
        }
    }

    private void updateSpinners() {
        int totalBots = botCards.stream().mapToInt(BotCardController::getValue).sum();
        int remaining = MAX_NUM_BOTS - totalBots;
        for(BotCardController card : botCards) {
            Spinner<Integer> spinner = card.getSpinner();
            int current = spinner.getValue();
            SpinnerValueFactory.IntegerSpinnerValueFactory vf = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
            vf.setMax(current + remaining);
        }
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
        _clientInfo.gameConfig._botsByType.clear();

        for(BotCardController card : botCards) {
            _clientInfo.gameConfig.setBotCount(card.getBotId(), card.getValue());
        }
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