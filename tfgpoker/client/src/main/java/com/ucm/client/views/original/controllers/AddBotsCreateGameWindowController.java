package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ucm.client.ClientInfo;
import com.ucm.common.BotDescriptor;
import com.ucm.common.BotRegistry;
import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


public class AddBotsCreateGameWindowController extends GenericController {
    
    private static final int MIN_NUM_BOTS = 0;

    private static final int MAX_NUM_BOTS = 8;
    private static final int MAX_NUM_BOTS_SPECTATOR = 9;

    private final List<BotAllowedStyleCardController> botCards = new ArrayList<>();

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnBackHome;

    @FXML
    private Button btnNextPlayers;

    @FXML
    private Button btnStartAddBots;

    @FXML
    private FlowPane botsContainer;

    @Override
    protected void onViewShown() {

        System.out.println(
            "Available bots: " + BotRegistry.getAvailableBots().size()
        );
        
        boolean allowBots = _clientInfo.gameConfig._allowBots;
        
        btnStartAddBots.setDisable(true);

        botsContainer.getChildren().clear();
        botCards.clear();

        for(BotDescriptor bot: BotRegistry.getAvailableBots()) {
            if(bot.allowStyles()){
                loadBotAllowedStyleCard(bot, allowBots);
            }
            else{

            }
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

    private void loadBotAllowedStyleCard(BotDescriptor bot, boolean allowBots){
        try {
                
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/original/fxml/BotAllowedStyleCard.fxml"));

            StackPane card = loader.load();
            BotAllowedStyleCardController controller = loader.getController();
            controller.setup(bot, isSpectator() ? MAX_NUM_BOTS_SPECTATOR : MAX_NUM_BOTS, allowBots);
            for(BotStyle style : BotStyle.values()){
                int value = _clientInfo.gameConfig.getBotStyleCount(bot.botId(), style);
                controller.setInitialStylesValue(style, value);
            }
            controller.updateBotCountByType();
            controller.setOnValueChanged(this::updateSpinnersAllowedStyles);

            botCards.add(controller);
            botsContainer.getChildren().add(card);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        updateSpinnersAllowedStyles();
    }

    private void saveAddBotsConfig() {
        _clientInfo.gameConfig._botsByType.clear();

        for(BotAllowedStyleCardController card : botCards) {
            _clientInfo.gameConfig.setBotCount(card.getBotId(), card.getValue());

            Map<BotStyle, Integer> styles = card.getStyleDistribution();

            for(var entry : styles.entrySet()){
                _clientInfo.gameConfig.setBotStyleCount(card.getBotId(), entry.getKey(), entry.getValue());
            }
        }
    }

    private void updateSpinnersAllowedStyles() {
        int maxBots = isSpectator() ? MAX_NUM_BOTS_SPECTATOR : MAX_NUM_BOTS;
        int totalBots = getTotalBotsByStyles();
        int remaining = maxBots - totalBots;
        for(BotAllowedStyleCardController card : botCards){
            card.updateStyleSpinners(remaining);
        }
    }

    private boolean isSpectator () {
        return _clientInfo.gameConfig._joinedAsSpectator;
    }

    private int getTotalBotsByStyles() {
        return botCards.stream().mapToInt(BotAllowedStyleCardController::getValue).sum();
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