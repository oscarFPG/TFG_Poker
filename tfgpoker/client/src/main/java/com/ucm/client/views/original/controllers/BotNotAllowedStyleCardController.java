package com.ucm.client.views.original.controllers;

import java.util.Map;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;

/**
 * Controller for the bot card that does not allow any style selection.
 */
public class BotNotAllowedStyleCardController extends BotCardController{
    
    /**
     * Constructor for the BotNotAllowedStyleCardController.
     * Initializes the controller with no style selection allowed.
     */
    public BotNotAllowedStyleCardController() {
        super(false);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(BotDescriptor bot, int maxBots, boolean allowBots) {

        setupCommon(bot);

        this.setInitialValue(BotStyle.DEFAULT, 0);
        // Controller for bot
        BotStyleRowController controller = new BotStyleRowController(bot, BotStyle.DEFAULT);
        controller.setup(BotStyle.DEFAULT, maxBots, allowBots);
        controller.setOnValueChanged(() -> {
            
            if(this.getRunnable() != null) 
                this.getRunnable().run();
        });

        // Add view to the bot card
        this.addSpinner(controller);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<BotStyle, Integer> getStyleDistribution(){return Map.of();}
}
    
