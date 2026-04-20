package com.ucm.client.utils;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertManager {

    private static Stage stage;

    public enum AlertTypeCustom {
        ERROR, WARNING, SUCCESS, INFO
    }

    public static void init(Stage s) {
        stage = s;
    }

    public static void show(String titleText, String messageText, AlertTypeCustom type) {
        showInternal(titleText, messageText, type, false);
    }

    public static boolean showConfirm(String titleText, String messageText, AlertTypeCustom type) {
        return showInternal(titleText, messageText, type, true);
    }

    private static boolean showInternal(String titleText, String messageText, AlertTypeCustom type, boolean withCancel) {

      
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(stage);
        
      
       alert.initStyle(StageStyle.TRANSPARENT); 

       

        DialogPane pane = alert.getDialogPane();

      
        pane.setHeader(null);
        pane.setGraphic(null);
        pane.setHeaderText(null); 

      
        Label icon = new Label();
        icon.getStyleClass().add("dialog-icon");

        switch (type) {
            case ERROR -> icon.setText("❌");
            case WARNING -> icon.setText("⚠");
            case SUCCESS -> icon.setText("✔");
            case INFO -> icon.setText("ℹ");
        }

      
        Label title = new Label(titleText);
        title.getStyleClass().add("dialog-title");
        title.setAlignment(Pos.CENTER); 
        title.setWrapText(true); 

       
        Label message = new Label(messageText);
        message.setWrapText(true);
        message.getStyleClass().add("dialog-message");
        message.setAlignment(Pos.CENTER); 

   
        VBox textBox = new VBox(title, message);
        textBox.setSpacing(0); 
        textBox.setAlignment(Pos.CENTER);

    
        VBox content = new VBox(icon, textBox);
        content.setSpacing(-10); 
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(380); 

        pane.setContent(content);

     
        ButtonType okBtn = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = null;

        if (withCancel) {
            cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            pane.getButtonTypes().setAll(cancelBtn, okBtn);
        } else {
            pane.getButtonTypes().setAll(okBtn);
        }

      
        String cssPath = AlertManager.class.getResource("/original/css/style.css").toExternalForm();
        if (cssPath != null) {
             pane.getStylesheets().add(cssPath);
        } else {
            System.err.println("❌ No se pudo cargar el archivo CSS de alertas.");
        }

       
        Button ok = (Button) pane.lookupButton(okBtn);
        ok.getStyleClass().add("dialog-confirm");
        
       
        ButtonBar buttonBar = (ButtonBar) pane.lookup(".button-bar");
        buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE); 
        
        if (withCancel && cancelBtn != null) {
            Button cancel = (Button) pane.lookupButton(cancelBtn);
            cancel.getStyleClass().add("dialog-cancel");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okBtn;
    }
}