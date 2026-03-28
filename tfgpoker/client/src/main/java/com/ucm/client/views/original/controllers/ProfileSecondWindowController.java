package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientMain;
import com.ucm.client.PokerGame;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ProfileSecondWindowController extends GenericController {

    
    PokerGame clientMain;

    @FXML
    private TextField ipLabel;

    @FXML
    private TextField nameLabel;

    @FXML
    private Button btnSave;

    @FXML
    public void initialize() {

        btnSave.setDisable(true);
        nameLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.trim().isEmpty()) {
                btnSave.setDisable(true);
            } 
            else {
                btnSave.setDisable(false);
            }
        });
    // btnSave.disableProperty()
    //     .bind(
    //         nameLabel.textProperty().isEmpty()
    //     );
    }

    @FXML
    private void conectar() {

        Platform.runLater(() -> {
            //ientMain.iniciar(ipLabel.getText(), nameLabel.getText() );
        });
        

        
    }
}
