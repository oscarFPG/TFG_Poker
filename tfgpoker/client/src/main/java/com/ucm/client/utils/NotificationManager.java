package com.ucm.client.utils;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NotificationManager {

    private static Stage stage;

    public static void init(Stage s) {
        stage = s;
    }

    public static void showError(String msg) {
        show(msg, false);
    }

    public static void showSuccess(String msg) {
        show(msg, true);
    }

    private static void show(String msg, boolean success) {

        Popup popup = new Popup();

        Label title = new Label(success ? "Success" : "Error");
        title.getStyleClass().addAll("notification-title");

      
        Label message = new Label(msg);
        message.getStyleClass().add("notification-message");
        message.setWrapText(true);
        message.setMaxWidth(260);

      
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("notification-close");

      
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox header = new HBox(title, spacer, closeBtn);
        header.setAlignment(Pos.CENTER_LEFT);

       
        VBox root = new VBox(header, message);
        root.setSpacing(4);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setMaxWidth(300);

        root.getStyleClass().add(success ? "notification-success" : "notification-error");

       
        root.getStylesheets().add(
            NotificationManager.class.getResource("/original/css/style.css").toExternalForm()
        );

        popup.getContent().add(root);

        popup.show(stage);

       
        root.applyCss();
        root.layout();

        double width = root.getWidth();

      
        double margin = 30;
        double titleBarHeight = stage.getHeight() - stage.getScene().getHeight();

        double x = stage.getX() + stage.getWidth() - width - margin;
        double y = stage.getY() + titleBarHeight + margin;

        popup.setX(x);
        popup.setY(y);

     
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> popup.hide());
        delay.play();

       
        closeBtn.setOnAction(e -> popup.hide());
    }
}