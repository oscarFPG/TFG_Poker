package com.ucm.client.views.original.controllers;

import com.ucm.client.utils.AlertManager;
import com.ucm.client.utils.Messages;
import com.ucm.client.utils.NotificationManager;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller class for the main window of the application. 
 * It manages the user interface elements and their interactions, including the menu toggle, content display, and card flipping animations.
 */
public class MainWindowController extends GenericController {

    /**
     * ToggleButton for the introduction section of the menu. 
     * When selected, it displays the introduction content in the main window.
     */
    @FXML
    private ToggleButton introductionIcon;
    /**
     * ToggleButton for the information section of the menu.
     */
    @FXML
    private ToggleButton informationIcon;
    /**
     * ToggleButton for the exit option in the menu.
     */
    @FXML
    private ToggleButton exitIcon;
    /**
     * ImageView for displaying the user's avatar in the profile section of the main window.
     */
    @FXML  
    private ImageView imgAvatarProfile;
    /**
     * Text element for displaying information in the main window.
     */
    @FXML
    private Text informationLabel;
    /**
     * Accordion component for displaying collapsible information sections in the main window.
     */
    @FXML
    private Accordion acordeonInformation;
    /**
     * AnchorPane that serves as the container for the swing bar in the main window.
     */
    @FXML
    private AnchorPane swingBar;
    /**
     * Button that toggles the visibility of the swing bar in the main window.
     */
    @FXML
    private Button swingBarButton;
    /**
     * BorderPane that serves as the container for the order pane panel in the main window.
     */
    @FXML
    private BorderPane orderPanePanel;
    /**
     * Button that saves the current state of the main window or performs a save action.
     */
    @FXML
    private Button btnSaveMain;
    /**
     * Text element that serves as a placeholder for displaying the player's name in the main window.
     */
    @FXML
    private Text playerNamePlaceholder;
    /**
     * VBox that contains the swing bar content in the main window.
     */
    @FXML
    private VBox vboxSwing;
    /**
     * StackPane that contains the paragraph label content in the main window.
     */
    @FXML
    private StackPane paragraphLabelStack;
    /**
     * ToggleGroup that manages the selection of the menu toggle buttons in the main window.
     */
    @FXML
    private ToggleGroup group1;
    /**
     * StackPane that contains the card information content in the main window.
     */
    @FXML
    private StackPane cardInfo;
    /**
     * VBox that represents the front side of the card in the main window.
     */
    @FXML
    private VBox cardFront;
    /**
     * StackPane that represents the back side of the card in the main window.
     */
    @FXML
    private StackPane cardBack;
    /**
     * Flag indicating whether the swing bar menu is currently open or closed.
     */
    private boolean isOpen = false;
    /**
     * Flag indicating whether the card is currently flipped or not.
     */
    private boolean isFlipped = false;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onViewShown() {
        cardBack.setRotate(180);
        imgAvatarProfile.setImage(_clientInfo.getAvatar(_clientInfo.name,64));
        setupInitialState();
        setupListeners();
    }
    /**
     * Sets up the initial state of the main window, including the visibility of UI elements and the selection of the default toggle button.
     */
    private void setupInitialState() {
        isOpen = false;
        swingBar.setPrefWidth(110);
        vboxSwing.setVisible(false);
        paragraphLabelStack.setVisible(false);
        group1.selectToggle(introductionIcon);
    }
    /**
     * Sets up listeners for the toggle buttons in the menu. When a toggle button is selected, it updates the content displayed in the main window accordingly.
     */
    private void setupListeners() {

        group1.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
            group1.selectToggle(oldToggle);
            return;
        }
            updateContent(newToggle);
        });
    }
    /**
     * Updates the content displayed in the main window based on the selected toggle button. 
     * It shows or hides relevant UI elements and handles the exit confirmation dialog.
     * @param newToggle
     */
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

            boolean confirmed = AlertManager.showConfirm(
                Messages.Confirm.EXIT_TITLE, 
                Messages.Confirm.EXIT_MSG, 
                AlertManager.AlertTypeCustom.WARNING
            );

            if (confirmed) {

                NotificationManager.closeAll();
                _stage.close();
                System.exit(0); 
            } else {

                group1.selectToggle(introductionIcon);
            }
        }
    }
    /**
     * Toggles the visibility of the swing bar menu in the main window.
     */
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
    /**
     * Flips the card in the main window, showing either the front or back side with a rotation animation.
     */
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
    /**
     * Handles the action of choosing a game from the main window. 
     * It triggers the transition to the next view or game selection process.
     */
    @FXML
    private void chooseGame() {
        next();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMainController(MainController mainController, Stage stage) {
        super.setMainController(mainController, stage);
        playerNamePlaceholder.setText(_clientInfo.name);
    }
}
