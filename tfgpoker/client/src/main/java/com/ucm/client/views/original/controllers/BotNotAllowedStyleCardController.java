package com.ucm.client.views.original.controllers;

import java.util.Map;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;
import com.ucm.common.GameType;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;

public class BotNotAllowedStyleCardController extends BotCardController{
    
    private static final int MIN_NUM_BOTS = 0;



    public BotNotAllowedStyleCardController() {
        super(false);

    }


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

    @Override
    public Map<BotStyle, Integer> getStyleDistribution(){return Map.of();}
}
    
