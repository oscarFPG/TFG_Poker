package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeCreateGameWindowController extends GenericController {

    @FXML
    private Button btnBackChooseGame;

    @FXML
    public void returnChooseGame(){
        back();
    }

    @Override
    public void onNextEvent() {
    }

    @Override
    public void onBackEvent() {
    }

}