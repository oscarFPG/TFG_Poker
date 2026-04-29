package com.ucm.client.views.original.controllers;

import com.ucm.common.BotDescriptor;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;

public class BotCardController {

    private static final int MIN_NUM_BOTS = 0;

    @FXML
    private Spinner<Integer> spinnerBot;
    @FXML
    private StackPane cardInfoBot;
    @FXML
    private VBox cardFrontBot;
    @FXML
    private StackPane cardBackBot;
    @FXML
    private Text textBotDescription;
    @FXML
    private Label labelBotName;
    @FXML
    private ImageView imgBot;

    private boolean isFlippedBot = false;
    private BotDescriptor descriptor;
    private Runnable onValueChanged;

    public void setup(BotDescriptor bot, int maxBots, boolean allowBots) {
        
        this.descriptor = bot;

        labelBotName.setText(bot.botName());
        textBotDescription.setText(bot.fullDescription());
        imgBot.setImage(new Image(getClass().getResource(bot.iconPath()).toExternalForm()));

        spinnerBot.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, maxBots, 0));
        spinnerBot.setVisible(!allowBots);
        spinnerBot.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(onValueChanged != null) onValueChanged.run();
        });

        cardBackBot.setVisible(false);
        cardFrontBot.setVisible(true);
    }

    public int getValue() {
        return spinnerBot.getValue();
    }

    public int getBotId() {
        return descriptor.botId();
    }

    public Spinner<Integer> getSpinner() {
        return spinnerBot;
    }

    public void setOnValueChanged(Runnable r) {
        this.onValueChanged = r;
    }

    public void setInitialValue(int value) {
        spinnerBot.getValueFactory().setValue(value);
    }

    @FXML
    private void flipCardBot() {
        
        Node visible = isFlippedBot ? cardBackBot : cardFrontBot;
        Node hidden = isFlippedBot ? cardFrontBot : cardBackBot;

        RotateTransition first = new RotateTransition(Duration.millis(350), visible);
        first.setFromAngle(0);
        first.setToAngle(90);
        first.setAxis(Rotate.Y_AXIS);
        first.setInterpolator(javafx.animation.Interpolator.EASE_IN);

        RotateTransition second = new RotateTransition(Duration.millis(200), hidden);
        second.setFromAngle(-90);
        second.setToAngle(0);
        second.setAxis(Rotate.Y_AXIS);
        second.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        first.setOnFinished(e -> {
            visible.setVisible(false);
            hidden.setVisible(true);
            second.play();
        });

        first.play();
        isFlippedBot = !isFlippedBot;
    }
}
