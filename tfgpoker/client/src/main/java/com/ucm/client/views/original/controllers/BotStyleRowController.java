package com.ucm.client.views.original.controllers;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * Controller for a row in the bot style selection UI.
 * It manages the label and spinner for a specific bot style.
 */
public class BotStyleRowController {
    /**
     * Spinner for selecting the number of bots of this style.
     */
    private Spinner<Integer> _spinnerBotStyle;
    /**
     * Label displaying the name of the bot style.
     */
    private Label _labelBotStyle;
    /**
     * BotDescriptor representing the bot associated with this row.
     */
    private BotDescriptor _bot;
    /**
     * BotStyle representing the style of the bot associated with this row.
     */
    private BotStyle _style;
    /**
     * Runnable to be executed when the value of the spinner changes.
     */
    private Runnable _onValueChanged;

    /**
     * Constructor for the BotStyleRowController.
     * @param bot
     * @param style
     */
    public BotStyleRowController(BotDescriptor bot, BotStyle style) {
        _bot = bot;
        _style = style;

        _labelBotStyle = new Label();
        _spinnerBotStyle = new Spinner<Integer>();
    }

    /**
     * Sets up the controller with the given bot style, maximum number of bots, and whether bots are allowed.
     * @param style
     * @param maxBots
     * @param allowBots
     */
    public void setup(BotStyle style, int maxBots, boolean allowBots) {

        _style = style;
        _labelBotStyle.setText(style.name());
        _spinnerBotStyle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxBots, 0));
        _spinnerBotStyle.setDisable(!allowBots);
        _spinnerBotStyle.valueProperty().addListener((obs, oldValue, newValue) -> {
            if(_onValueChanged != null){
                _onValueChanged.run();
            }
        });
    } 
    /**
     * Returns the label associated with this bot style row.
     * @return Label displaying the name of the bot style.
     */
    public Label getLabel(){
        return _labelBotStyle;
    }
    /**
     * Returns the bot style associated with this row.
     * @return BotStyle representing the style of the bot.
     */
    public BotStyle getStyle(){
        return _style;
    }
    /**
     * Returns the current value of the spinner, representing the number of bots selected for this style.
     * @return int representing the number of bots selected for this style.
     */
    public int getValue() {
        return _spinnerBotStyle.getValue();
    }
    /**
     * Sets the value of the spinner, representing the number of bots selected for this style.
     * @param value
     */
    public void setValue(int value){
        _spinnerBotStyle.getValueFactory().setValue(value);
    }
    /**
     * Returns the spinner associated with this bot style row.
     * @return Spinner<Integer> for selecting the number of bots of this style.
     */
    public Spinner<Integer> getSpinner(){
        return _spinnerBotStyle;
    }
    /**
     * Sets a Runnable to be executed when the value of the spinner changes.
     * @param r
     */
    public void setOnValueChanged(Runnable r){
        _onValueChanged = r;
    }

}
