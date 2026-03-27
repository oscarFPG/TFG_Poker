package com.ucm.client.controllers;

import com.ucm.client.ClientMain;
import com.ucm.client.ClientMainCopy;
import com.ucm.client.ClientService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class profileSecondWindowController {

    
      ClientMainCopy clientMain;
    
         @FXML
        private TextField ipLabel;

         @FXML
        private TextField nameLabel;

        @FXML
        private Button btnSave;

        @FXML
        public void initialize() {

            btnSave.disableProperty().bind(
                ipLabel.textProperty().isEmpty()
                    .or(nameLabel.textProperty().isEmpty())
            );
}

        @FXML
        private void conectar() {

            clientMain.iniciar(ipLabel.getText(),nameLabel.getText() );

            
        }
}
