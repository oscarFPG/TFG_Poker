package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddCardsCreateGameWindowController extends GenericController {
    
    @FXML
    private Button btnBackTable;

    @FXML
    public void jumpToTable(){
        back();
    }

    @Override
    public void onNextEvent() {
       
    }

    @Override
    public void onBackEvent() {
        
    }
    
}
