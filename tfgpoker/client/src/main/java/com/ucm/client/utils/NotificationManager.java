package com.ucm.client.utils;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

public class NotificationManager extends DialogsManager {
    
    private static final List<Popup> activeNotifications = new ArrayList<>();
    private static final double MARGIN = 20;
    private static final double SPACING = 10;

    public static void showError(String msg) {
        show(msg, "Error", "notification-error");
    }

    public static void showSuccess(String msg) {
        show(msg, "Success", "notification-success");
    }

    /**
     * Cierra todas las notificaciones activas de forma segura.
     */
    public static void closeAll() {
        runSafe(() -> {
            List<Popup> copy = new ArrayList<>(activeNotifications);
            for (Popup p : copy) {
                p.hide();
            }
            activeNotifications.clear();
        });
    }

    private static void show(String msg, String titleText, String styleClass) {
        runSafe(() -> {
            // Límite de 5 notificaciones
            if (activeNotifications.size() > 5) {
                closeNotification(activeNotifications.get(0));
            }

            Popup popup = new Popup();
            
            // --- MEJORA AUTOMÁTICA DE CIERRE ---
            // Vinculamos el ciclo de vida del popup al Stage principal.
            // Si el stage deja de mostrarse (isShowing = false), cerramos las notificaciones.
            if (stage != null) {
                stage.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                    if (!isShowing) {
                        closeAll();
                    }
                });
            }
            // -----------------------------------

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

            applyCSS(root);
            popup.getContent().add(root);

            activeNotifications.add(popup);
            
            if (stage != null && stage.isShowing()) {
                popup.show(stage);
                updatePositions();
            }

            // Auto-cierre tras 5 segundos
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(e -> closeNotification(popup));
            delay.play();

            closeBtn.setOnAction(e -> closeNotification(popup));
        });
    }

    private static void closeNotification(Popup popup) {
        runSafe(() -> {
            if (activeNotifications.remove(popup)) {
                popup.hide();
                updatePositions(); 
            }
        });
    }

    private static void updatePositions() {
        // Si la ventana principal no es visible, no tiene sentido posicionar
        if (stage == null || !stage.isShowing()) return;

        double titleBarHeight = stage.getHeight() - stage.getScene().getHeight();
        double currentY = stage.getY() + titleBarHeight + MARGIN;
        
        List<Popup> copy = new ArrayList<>(activeNotifications);

        for (Popup p : copy) {
            if (p == null || !p.isShowing()) continue;

            Region content = (Region) p.getContent().get(0);
            
            // Forzamos el renderizado para obtener dimensiones reales
            content.applyCss();
            content.layout();

            double x = stage.getX() + stage.getWidth() - content.getWidth() - MARGIN;
            p.setX(x);
            p.setY(currentY);

            currentY += content.getHeight() + SPACING;
        }
    }
}