package com.ucm.client.views.original.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotRegistry;
import com.ucm.common.BotStyle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;


public class AddBotsCreateGameWindowController extends GenericController {
    
    private static final int MAX_NUM_BOTS = 8;
    private static final int MAX_NUM_BOTS_SPECTATOR = 9;


    private List<BotCardController> botCards = new ArrayList<>();

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

        // Restart previous information
        btnStartAddBots.setDisable(true);
        botsContainer.getChildren().clear();
        botCards.clear();

        // Generate every bot card to select and amount
        for(BotDescriptor bot : BotRegistry.getAvailableBots()) {
            loadBotCard(bot, _clientInfo.gameConfig._allowBots);
        }
        
        updateAllSpinners();
    }

    private void loadBotCard(BotDescriptor bot, boolean allowBots) {

        try {

            // Load bot card -> Bot type + style selector
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/original/fxml/botCard.fxml"));
            BotCardController controller = bot.allowStyles() ? 
                    new BotAllowedStyleCardController() : new BotNotAllowedStyleCardController();
            
            loader.setController(controller);
            botsContainer.getChildren().add( loader.load() );

            // Set up specific controller for the specific bot
            int maximumBotCount = isSpectator() ? MAX_NUM_BOTS_SPECTATOR : MAX_NUM_BOTS;
            controller.setup(bot, maximumBotCount, allowBots);

            // Initialize previous values for each bot + style or base bot type
            int previousValue;
            if(bot.allowStyles()) {

                for(BotStyle style : BotStyle.values()) {
                    previousValue = _clientInfo.gameConfig.getBotStyleCount(bot.botId(), style);
                    controller.setInitialValue(style, previousValue);
                }
            }
            else {
                previousValue = _clientInfo.gameConfig.getBotCount(bot.botId());
                controller.setInitialValue(BotStyle.DEFAULT, previousValue);
            }

            // Link event listener to update each spinner for each bot card when interacting
            controller.setOnValueChanged(this::updateAllSpinners);

            // Link controller, save specific controller and add card to the placeholder
            botCards.add(controller);
            
        }
        catch(Exception e) {
            System.out.printf("ERROR loading bot card for %s: %s\n", bot.botName(), e.getMessage());
        }
    }

    public void updateAllSpinners() {

        // Update remaining bots quantity
        int totalBots = 0;
        int remaining = isSpectator() ? MAX_NUM_BOTS_SPECTATOR : MAX_NUM_BOTS;
        for(BotCardController card : botCards) {
            totalBots += card.getValue();
        }
        remaining -= totalBots;

        // Update every spinner with the new remaining amount
        for(BotCardController ctrl : botCards) {
            ctrl.updateSpinners(remaining);
        }
    }

    private void saveAddBotsConfig() {
        
        // Clear previous info
        _clientInfo.gameConfig._botsByType.clear();

        // Save current bot configuration
        for(BotCardController card : botCards) {

            // Always add to the general purpouse bot count -> Styles NOT specified
            _clientInfo.gameConfig.setBotCount(card.getBotId(), card.getValue());

            // Add to the specific bot count if necessary
            if(card._allowStyle) {

                Map<BotStyle, Integer> styles = card.getStyleDistribution();
                for(var entry : styles.entrySet()) {
                    _clientInfo.gameConfig.setBotStyleCount(card.getBotId(), entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private boolean isSpectator() {
        return _clientInfo.gameConfig._joinedAsSpectator;
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


    @Override
    public void onNextEvent() {
        saveAddBotsConfig();
    }

    @Override
    public void onBackEvent() {
        saveAddBotsConfig();
    }

}