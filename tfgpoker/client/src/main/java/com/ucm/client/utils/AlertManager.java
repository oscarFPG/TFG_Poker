package com.ucm.client.utils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class AlertManager extends DialogsManager {
    
    private static boolean isAlertShowing = false;

    public enum AlertTypeCustom { ERROR, WARNING, SUCCESS, INFO }

    public static void show(String title, String message, AlertTypeCustom type) {
        if (isAlertShowing) return;
        runSafe(() -> executeAlert(title, message, type, false));
    }

    public static boolean showConfirm(String title, String message, AlertTypeCustom type) {
        if (isAlertShowing) return false;

        if (javafx.application.Platform.isFxApplicationThread()) {
            return executeAlert(title, message, type, true);
        } else {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            runSafe(() -> future.complete(executeAlert(title, message, type, true)));
            try { 
                return future.get(); 
            } catch (Exception e) { 
                return false; 
            }
        }
    }

    private static boolean executeAlert(String title, String msg, AlertTypeCustom type, boolean withCancel) {
        isAlertShowing = true;
        try {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.initOwner(stage);
            alert.initStyle(StageStyle.TRANSPARENT);

            DialogPane pane = alert.getDialogPane();
            pane.sceneProperty().addListener((obs, old, n) -> { if(n != null) n.setFill(Color.TRANSPARENT); });
            
            pane.setHeader(null);
            pane.setGraphic(null);
            pane.setContent(buildVisualContent(title, msg, type));

            setupButtons(pane, withCancel);
            applyCSS(pane);

            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
        } finally {
            isAlertShowing = false;
        }
    }

    private static VBox buildVisualContent(String titleText, String messageText, AlertTypeCustom type) {
        Label icon = new Label(getIconEmoji(type));
        icon.getStyleClass().add("dialog-icon");

        Label title = new Label(titleText);
        title.getStyleClass().add("dialog-title");
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        Label message = new Label(messageText);
        message.getStyleClass().add("dialog-message");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);

        VBox content = new VBox(icon, title, message);
        content.setSpacing(10);
        content.setAlignment(Pos.CENTER);
        content.setMinWidth(280);
        return content;
    }

    private static void setupButtons(DialogPane pane, boolean withCancel) {
        ButtonType okBtn = new ButtonType("Acept", ButtonBar.ButtonData.OK_DONE);
        if (withCancel) {
            ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            pane.getButtonTypes().setAll(cancelBtn, okBtn);
            ((Button) pane.lookupButton(cancelBtn)).getStyleClass().add("dialog-cancel");
        } else {
            pane.getButtonTypes().setAll(okBtn);
        }
        ((Button) pane.lookupButton(okBtn)).getStyleClass().add("dialog-confirm");
        
        ButtonBar buttonBar = (ButtonBar) pane.lookup(".button-bar");
        if (buttonBar != null) buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
    }

     private static String getIconEmoji(AlertTypeCustom type) {
        return switch (type) {
            case ERROR -> "❌";
            case WARNING -> "⚠";
            case SUCCESS -> "✔";
            case INFO -> "ℹ";
        };
    }
}