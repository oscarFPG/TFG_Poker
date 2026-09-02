package com.ucm.client.views.original.controllers;



import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;

import javafx.scene.control.Label;

/**
 * Controller for a card that allows the user to select the number of bots of each style.
 */
public class BotAllowedStyleCardController extends BotCardController {

    /**
     * Label to display the total number of bots selected
     */
    private Label _labelBotCount;
    /**
     * constructor for the BotAllowedStyleCardController class.
     */
    public BotAllowedStyleCardController() {
        super(true);

        _labelBotCount = new Label();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(BotDescriptor bot, int maxBots, boolean allowBots) {

        setupCommon(bot);

        for(BotStyle style : BotStyle.values()) {
            
            // Controller for every bot + style combination
            BotStyleRowController controller = new BotStyleRowController(bot, style);
            controller.setup(style, maxBots, allowBots);
            controller.setOnValueChanged(() ->{
                
                updateBotCountLabel();
                if(this.getRunnable() != null) 
                    this.getRunnable().run();
            });
           
            // Save this controller
            this.addSpinner(controller);
        }
    }
    /**
     * Updates the label that shows the total number of bots selected.
     */
    private void updateBotCountLabel() {
        _labelBotCount.setText(String.valueOf(getValue()));
    }

}