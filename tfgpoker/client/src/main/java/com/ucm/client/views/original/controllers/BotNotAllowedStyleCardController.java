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


    @FXML
    private Spinner<Integer> spinnerBot;


    public BotNotAllowedStyleCardController() {
        super(false);
    }


    @Override
    public void setup(BotDescriptor bot, int maxBots, boolean allowBots) {

        setupCommon(bot);

        spinnerBot.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, maxBots, 0));
        spinnerBot.setDisable(!allowBots);
        spinnerBot.valueProperty().addListener((obs, oldVal, newVal) -> {
            
            if(this.getRunnable() != null) 
                this.getRunnable().run();
        });
    }

    @Override
    public int getValue() {
        return spinnerBot.getValue();
    }

    @Override
    public Map<BotStyle, Integer> getStyleDistribution(){return Map.of();}

    @Override
    public void setInitialValue(BotStyle style, int value) {
       spinnerBot.getValueFactory().setValue(value);
    }

    public Spinner<Integer> getSpinner() {
        return spinnerBot;
    }

    @Override
    public void updateSpinners(int remaining) {
        int current = spinnerBot.getValue();
        SpinnerValueFactory.IntegerSpinnerValueFactory vf = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinnerBot.getValueFactory();
        vf.setMax(current + remaining);
    }
}
    
