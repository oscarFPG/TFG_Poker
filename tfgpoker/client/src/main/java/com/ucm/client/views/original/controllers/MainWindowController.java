package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class MainWindowController extends GenericController {

    @FXML
    private ToggleButton introductionIcon;

    @FXML
    private ToggleButton informationIcon;

    @FXML
    private ToggleButton exitIcon;

    @FXML
    private Text informationLabel;

    @FXML
    private Accordion acordeonInformation;

    @FXML
    private AnchorPane swingBar;

    @FXML
    private Button swingBarButton;

    @FXML
    private BorderPane orderPanePanel;

    private boolean isOpen = false;

    @FXML
    private Button btnSaveMain;

    @FXML
    private Text playerNamePlaceholder;

    @FXML
    private VBox vboxSwing;

    @FXML
    private StackPane paragraphLabelStack;

    @FXML
    private ToggleGroup group1;

    @FXML
    private StackPane cardInfo;

    @FXML
    private VBox cardFront;

    @FXML
    private StackPane cardBack;

    private boolean isFlipped = false;


    @FXML
    public void initialize() {
        cardBack.setRotate(180);
        setupInitialState();
        setupListeners();
    }

    private void setupInitialState() {
        isOpen = false;
        swingBar.setPrefWidth(110);
        vboxSwing.setVisible(false);
        paragraphLabelStack.setVisible(false);
        group1.selectToggle(introductionIcon);
    }

    private void setupListeners() {

        group1.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
            group1.selectToggle(oldToggle);
            return;
        }
            updateContent(newToggle);
        });
    }

    private void updateContent(Toggle newToggle) {

        if (!isOpen || newToggle == null) return;
        ToggleButton selected = (ToggleButton) newToggle;

        if (selected == introductionIcon) {
            informationLabel.setVisible(true);
            informationLabel.setText("INTRODUCTION");

            acordeonInformation.setVisible(false);
            paragraphLabelStack.setVisible(true);

        } else if (selected == informationIcon) {
            informationLabel.setVisible(true);
            informationLabel.setText("INFORMATION");

            acordeonInformation.setVisible(true);
            paragraphLabelStack.setVisible(false);

        } else if (selected == exitIcon) {
            informationLabel.setVisible(false);
            acordeonInformation.setVisible(false);
            paragraphLabelStack.setVisible(false);
        }
    }


    @FXML
    private void toggleMenu() {

        isOpen = !isOpen;

        double targetWidth = isOpen ? 395 : 110;

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(300),
                new KeyValue(swingBar.prefWidthProperty(), targetWidth)
            )
        );

        timeline.play();

        vboxSwing.setVisible(isOpen);
        paragraphLabelStack.setVisible(isOpen);

        if (isOpen) {
          
            group1.selectToggle(introductionIcon);
            updateContent(introductionIcon);
        }
    }

    @FXML
    private void flipCard() {

        if (!isFlipped) {

            RotateTransition first = new RotateTransition(Duration.millis(200), cardFront);
            first.setFromAngle(0);
            first.setToAngle(90);
            first.setAxis(Rotate.Y_AXIS);

            RotateTransition second = new RotateTransition(Duration.millis(200), cardBack);
            second.setFromAngle(-90);
            second.setToAngle(0);
            second.setAxis(Rotate.Y_AXIS);

            first.setOnFinished(e -> {
                cardFront.setVisible(false);
                cardBack.setVisible(true);
                second.play();
            });

            first.play();

        } else {

            RotateTransition first = new RotateTransition(Duration.millis(200), cardBack);
            first.setFromAngle(0);
            first.setToAngle(90);
            first.setAxis(Rotate.Y_AXIS);

            RotateTransition second = new RotateTransition(Duration.millis(200), cardFront);
            second.setFromAngle(-90);
            second.setToAngle(0);
            second.setAxis(Rotate.Y_AXIS);

            first.setOnFinished(e -> {
                cardBack.setVisible(false);
                cardFront.setVisible(true);
                second.play();
            });

            first.play();
        }

        isFlipped = !isFlipped;
    }
    
    @FXML
    private void chooseGame() {
        next();
    }

    @Override
    public void onNextEvent() {}


    @Override
    public void onBackEvent() {}

    @Override
    public void setMainController(MainController mainController) {
        super.setMainController(mainController);
        playerNamePlaceholder.setText(_clientInfo.name);
    }

   

}
