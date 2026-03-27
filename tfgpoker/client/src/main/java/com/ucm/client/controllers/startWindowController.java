package com.ucm.client.controllers;

import com.ucm.client.ClientMain;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class startWindowController {

    ClientMain clientMain;
    
    @FXML
    private Button startButton;

    @FXML
    private void abrirNuevaVentana(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucm/client/views/original/fxml/profileSecondWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Configuración del Juego");
            stage.setScene(scene);
            stage.show();
            ((Stage) startButton.getScene().getWindow()).close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
