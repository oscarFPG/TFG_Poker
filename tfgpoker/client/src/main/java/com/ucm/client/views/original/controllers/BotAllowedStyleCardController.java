package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;
import com.ucm.common.GameType;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;

public class BotAllowedStyleCardController extends BotCardController {

    private Label labelBotCount;


    public BotAllowedStyleCardController() {
        super(true);
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
        labelBotCount.setText(String.valueOf(getValue()));
    }

    public void updateBotCountByType(){
        updateBotCountLabel();
    }

}