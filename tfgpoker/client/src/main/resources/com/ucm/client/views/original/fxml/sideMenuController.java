package com.ucm.client.views.original.fxml;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class sideMenuController {

    @FXML private VBox content1;
    @FXML private VBox content2;
    @FXML private VBox content3;

    @FXML private Label arrow1;
    @FXML private Label arrow2;
    @FXML private Label arrow3;

    private final double ANIMATION_TIME = 200;
    private final double CONTENT_HEIGHT = 100;

    // ==============================
    // MÉTODO GENERAL (REUTILIZABLE)
    // ==============================
    private void toggle(VBox content, Label arrow) {

        boolean isOpen = content.isVisible();

        Timeline timeline = new Timeline();

        if (!isOpen) {
            // Cerrar los demás (modo acordeón real)
            closeAll();

            content.setVisible(true);

            KeyValue kv = new KeyValue(content.maxHeightProperty(), CONTENT_HEIGHT);
            KeyFrame kf = new KeyFrame(Duration.millis(ANIMATION_TIME), kv);

            timeline.getKeyFrames().add(kf);

            rotateArrow(arrow, 90);

        } else {
            KeyValue kv = new KeyValue(content.maxHeightProperty(), 0);
            KeyFrame kf = new KeyFrame(Duration.millis(ANIMATION_TIME), kv);

            timeline.getKeyFrames().add(kf);

            timeline.setOnFinished(e -> content.setVisible(false));

            rotateArrow(arrow, 0);
        }

        timeline.play();
    }

    // ==============================
    // ROTACIÓN SUAVE DE FLECHA 🔥
    // ==============================
    private void rotateArrow(Label arrow, double angle) {
        Timeline t = new Timeline(
            new KeyFrame(Duration.millis(ANIMATION_TIME),
                new KeyValue(arrow.rotateProperty(), angle))
        );
        t.play();
    }

    // ==============================
    // CERRAR TODOS (ACORDEÓN REAL)
    // ==============================
    private void closeAll() {
        close(content1, arrow1);
        close(content2, arrow2);
        close(content3, arrow3);
    }

    private void close(VBox content, Label arrow) {
        content.setMaxHeight(0);
        content.setVisible(false);
        arrow.setRotate(0);
    }

    // ==============================
    // EVENTOS FXML
    // ==============================
    @FXML private void toggle1() { toggle(content1, arrow1); }
    @FXML private void toggle2() { toggle(content2, arrow2); }
    @FXML private void toggle3() { toggle(content3, arrow3); }

    // ==============================
    // INICIALIZACIÓN
    // ==============================
    @FXML
    public void initialize() {
        // Asegurar estado inicial
        closeAll();
    }
}