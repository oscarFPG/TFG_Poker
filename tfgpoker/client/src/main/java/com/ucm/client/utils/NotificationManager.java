package com.ucm.client.utils;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
/**
 * This class manages the display of notifications in a JavaFX application. It provides methods to show error and success notifications, as well as to close all active notifications.
 * Notifications are displayed in a stack at the top-right corner of the main application window, and they automatically disappear after a set duration or when the user clicks the close button.
 */
public class NotificationManager extends DialogsManager {

    private static final List<Stage> activeNotifications = new ArrayList<>();
    private static final double MARGIN = 30;
    private static final double MARGIN_TOP = 60;
    private static final double SPACING = 10;
    private static boolean listenersAdded = false;
    /**
     * Displays an error notification with the specified message.
     * @param msg
     */
    public static void showError(String msg) {
        show(msg, "Error", "notification-error");
    }
    /**
     * Displays a success notification with the specified message.
     * @param msg
     */
    public static void showSuccess(String msg) {
        show(msg, "Success", "notification-success");
    }
    /**
     * Closes all active notifications.
     * This method is called when the main application window is minimized or closed, or when the user explicitly requests to close all notifications.
     */
    public static void closeAll() {
        runSafe(() -> {
            List<Stage> copy = new ArrayList<>(activeNotifications);
            for (Stage s : copy) {
                s.close();
            }
            activeNotifications.clear();
        });
    }
    /**
     * Displays a notification with the specified message, title, and style class.
     * If there are already four active notifications, the oldest one is closed to make room for the new notification.
     * @param msg
     * @param titleText
     * @param styleClass
     */
    private static void show(String msg, String titleText, String styleClass) {
        runSafe(() -> {

            if (stage == null || !stage.isShowing()) return;

           
            if (activeNotifications.size() >= 4) {
                closeNotification(activeNotifications.get(0));
            }

            Stage popup = new Stage();
            popup.initOwner(stage);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setAlwaysOnTop(false);

           
            Label title = new Label(titleText);
            title.getStyleClass().add("notification-title");

            Label message = new Label(msg);
            message.getStyleClass().add("notification-message");
            message.setWrapText(true);
            message.setMaxWidth(260);

            Button closeBtn = new Button("✕");
            closeBtn.getStyleClass().add("notification-close");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox header = new HBox(title, spacer, closeBtn);
            header.setAlignment(Pos.CENTER_LEFT);

            VBox root = new VBox(header, message);
            root.setSpacing(4);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setMinWidth(280);
            root.setMaxWidth(300);
            root.getStyleClass().add(styleClass);
            root.getStyleClass().add("notification-container");

            applyCSS(root);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            popup.setScene(scene);

           
            popup.show();
            activeNotifications.add(popup);

            addStageListeners();
            updatePositions();

          
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(e -> closeNotification(popup));
            delay.play();

            closeBtn.setOnAction(e -> closeNotification(popup));
        });
    }
    /**
     * Closes the specified notification and updates the positions of the remaining notifications.
     * @param popup
     */
    private static void closeNotification(Stage popup) {
        runSafe(() -> {
            if (activeNotifications.remove(popup)) {
                popup.close();
                updatePositions();
            }
        });
    }
    /**
     * Updates the positions of all active notifications to ensure they are stacked correctly in the top-right corner of the main application window.
     */
    private static void updatePositions() {
        if (stage == null || !stage.isShowing()) return;

        double currentY = stage.getY() + MARGIN_TOP;

        List<Stage> copy = new ArrayList<>(activeNotifications);

        for (Stage s : copy) {
            if (s == null || !s.isShowing()) continue;

            VBox content = (VBox) s.getScene().getRoot();

            content.applyCss();
            content.layout();

            double x = stage.getX() + stage.getWidth() - content.getWidth() - MARGIN;

            s.setX(x);
            s.setY(currentY);

            currentY += content.getHeight() + SPACING;
        }
    }
    /**
     * Adds listeners to the main application window (stage) to update the positions of notifications when the window is moved, resized, minimized, or closed.
     */
    private static void addStageListeners() {
        if (listenersAdded || stage == null) return;
        listenersAdded = true;

        stage.xProperty().addListener((obs, o, n) -> updatePositions());
        stage.yProperty().addListener((obs, o, n) -> updatePositions());
        stage.widthProperty().addListener((obs, o, n) -> updatePositions());
        stage.heightProperty().addListener((obs, o, n) -> updatePositions());

        
        stage.iconifiedProperty().addListener((obs, oldVal, isMinimized) -> {
            if (isMinimized) closeAll();
        });

      
        stage.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (!isShowing) closeAll();
        });
    }
}