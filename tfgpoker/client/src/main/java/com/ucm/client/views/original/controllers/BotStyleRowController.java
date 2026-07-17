package com.ucm.client.views.original.controllers;

import com.ucm.common.BotStyle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class BotStyleRowController {
    
    @FXML
    private Spinner<Integer> spinnerBotStyle;
    @FXML
    private Label labelBotStyle;

    private BotStyle _style;

    private Runnable _onValueChanged;

    public void setup(BotStyle style, int maxBots){
        _style = style;
        labelBotStyle.setText(style.name());
        spinnerBotStyle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxBots, 0));
        spinnerBotStyle.valueProperty().addListener((obs, oldValue, newValue) -> {
            if(_onValueChanged != null){
                _onValueChanged.run();
            }
        });
    }  

    public BotStyle getStyle(){
        return _style;
    }

    public int getValue() {
        return spinnerBotStyle.getValue();
    }

    public void setValue(int value){
        spinnerBotStyle.getValueFactory().setValue(value);
    }

    public Spinner<Integer> getSpinner(){
        return spinnerBotStyle;
    }

    public void setOnValueChanged(Runnable r){
        _onValueChanged = r;
    }

}
