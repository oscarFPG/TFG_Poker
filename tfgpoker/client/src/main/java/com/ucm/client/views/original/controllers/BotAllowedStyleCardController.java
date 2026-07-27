package com.ucm.client.views.original.controllers;



import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;

import javafx.scene.control.Label;


public class BotAllowedStyleCardController extends BotCardController {

    private Label _labelBotCount;

    public BotAllowedStyleCardController() {
        super(true);

        _labelBotCount = new Label();
    }

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

    private void updateBotCountLabel() {
        _labelBotCount.setText(String.valueOf(getValue()));
    }

}