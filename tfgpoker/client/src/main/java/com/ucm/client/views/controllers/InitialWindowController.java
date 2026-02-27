package com.ucm.client.views.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InitialWindowController {
    
    @FXML
    private Button btnPlay;

    @FXML
    public void initialize() {
        btnPlay.setOnAction(e -> abrirConfigurationWindow());
    }

    private void abrirConfigurationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigurationWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Configuración del Juego");
            stage.setScene(scene);
            stage.show();
            ((Stage) btnPlay.getScene().getWindow()).close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
