package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class MainWindowController extends GenericController {
    
    @FXML
    private Button btnSaveMain;

    @FXML
    private Text playerNamePlaceholder;


    @FXML
    public void initialize() {
        
        _clientInfo = ClientInfo.getInstance();
        playerNamePlaceholder.setText( _clientInfo.name );
    }
    

    @FXML
    private void chooseGame() {
        next();
    }

    @Override
    public void onNextEvent() {}


    @Override
    public void onBackEvent() {}

}
