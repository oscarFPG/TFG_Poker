package com.ucm.client.views.original.controllers;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class BotStyleRowController {
    
    private Spinner<Integer> _spinnerBotStyle;
    
    private Label _labelBotStyle;

    private BotDescriptor _bot;
    private BotStyle _style;

    private Runnable _onValueChanged;



    public BotStyleRowController(BotDescriptor bot, BotStyle style) {
        _bot = bot;
        _style = style;

        _labelBotStyle = new Label();
        _spinnerBotStyle = new Spinner<Integer>();
    }


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

    public BotStyle getStyle(){
        return _style;
    }

    public int getValue() {
        return _spinnerBotStyle.getValue();
    }

    public void setValue(int value){
        _spinnerBotStyle.getValueFactory().setValue(value);
    }

    public Spinner<Integer> getSpinner(){
        return _spinnerBotStyle;
    }

    public void setOnValueChanged(Runnable r){
        _onValueChanged = r;
    }

}
