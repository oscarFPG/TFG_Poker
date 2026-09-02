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

/**
 * Class that manages the display of alert dialogs in the application.
 * It provides methods to show different types of alerts, including error, warning, success, and informational messages.
 */
public class AlertManager extends DialogsManager {
    
    private static boolean isAlertShowing = false;

    public enum AlertTypeCustom { ERROR, WARNING, SUCCESS, INFO }
    /**
     * Displays an alert dialog with the specified title, message, and type.
     * @param title
     * @param message
     * @param type
     */
    public static void show(String title, String message, AlertTypeCustom type) {
        if (isAlertShowing) return;
        runSafe(() -> executeAlert(title, message, type, false));
    }
    /**
     * Displays a confirmation dialog with the specified title, message, and type.
     * Returns true if the user confirms, false otherwise.
     * @param title
     * @param message
     * @param type
     * @return boolean indicating whether the user confirmed the action
     */
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
    /**
     * Executes the alert dialog with the specified parameters.
     * @param title
     * @param msg
     * @param type
     * @param withCancel
     * @return boolean indicating whether the user confirmed the action
     */
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
    /**
     * Builds the visual content of the alert dialog, including the icon, title, and message.
     * @param titleText
     * @param messageText
     * @param type
     * @return VBox containing the visual content of the alert dialog
     */
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
    /**
     * Sets up the buttons for the alert dialog, including the OK and Cancel buttons based on the specified parameters.
     * @param pane
     * @param withCancel
     */
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
    /**
     * Applies the CSS styles to the alert dialog pane.
     * @param type
     * @return String representing the CSS class for the specified alert type
     */
    private static String getIconEmoji(AlertTypeCustom type) {
        return switch (type) {
            case ERROR -> "❌";
            case WARNING -> "⚠";
            case SUCCESS -> "✔";
            case INFO -> "ℹ";
        };
    }
}