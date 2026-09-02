package com.ucm.client.views.original.controllers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;


import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;


public abstract class BotCardController {

    protected static final int MIN_NUM_BOTS = 0;
    protected static final int MAX_COLUMN_STYLES = 2;

    /**
     * Label to display the bot's name
     */
    @FXML
    private Label labelBotName;         
    /**
     * VBox to hold the front side of the bot card
     */
    @FXML
    private VBox cardFrontBot;
    /**
     * StackPane to hold the back side of the bot card
     */
    @FXML
    private StackPane cardBackBot;
    /**
     * StackPane to display bot information
     */
    @FXML
    private StackPane cardInfoBot;
    /**
     * Text to display the bot's description
     */
    @FXML
    private Text textBotDescription;
    /**
     * ImageView to display the bot's image
     */
    @FXML
    private ImageView imgBot;
    /**
     * FlowPane to hold the spinners for each bot style
     */
    @FXML
    private FlowPane flowPaneBotStyles;
    /**
     * List to hold the BotStyleRowControllers associated with each spinner
     */
    private final List<BotStyleRowController> styleRows = new ArrayList<>();
    /**
     * Flag to indicate whether the bot card is flipped or not
     */
    private boolean isFlippedBot = false;
    /**
     * BotDescriptor associated with this BotCardController
     */
    private BotDescriptor _descriptor;
    /**
     * Runnable to be executed when the value of a spinner changes
     */
    private Runnable onValueChanged;
    /**
     * Flag to indicate whether styles are allowed or not
     */
    protected boolean _allowStyle;
    
    /**
     * Constructor for the BotCardController class.
     * @param allowStyles
     */
    public BotCardController(final boolean allowStyles) {
        _allowStyle = allowStyles;
    }

    /**
     * Adds a spinner to the flowPaneBotStyles and associates it with a BotStyleRowController.
     * @param ctrl
     */
    public void addSpinner(BotStyleRowController ctrl) {

        styleRows.add(ctrl);

        HBox groupStyle = new HBox();
        groupStyle.autosize();
        groupStyle.setSpacing(3);
        groupStyle.setAlignment(Pos.CENTER);

        Label label = ctrl.getLabel();
        label.setPrefWidth(110);
        label.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        label.getStyleClass().add("text-font-add-bots");

        Spinner<Integer> spinner = ctrl.getSpinner();
        spinner.setPrefWidth(80);
        spinner.setMinWidth( spinner.getPrefWidth() );
        spinner.getStyleClass().add("container-colors");

        groupStyle.getChildren().addAll(label, spinner);
        flowPaneBotStyles.getChildren().add(groupStyle);
    }
    /**
     * Calculates the total value of all BotStyleRowControllers in styleRows.
     * @return value of all BotStyleRowControllers in styleRows
     */
    public int getValue() {
        return styleRows.stream().mapToInt(BotStyleRowController::getValue).sum();
    }
    /**
     * Calculates the distribution of values for each BotStyle in styleRows.
     * @return a map containing the distribution of values for each BotStyle in styleRows
     */
    public Map<BotStyle, Integer> getStyleDistribution() {

        Map<BotStyle, Integer> distribution = new HashMap<>();
        for(BotStyleRowController row : styleRows){
            int value = row.getValue();

            if(value > 0) {
                distribution.put(row.getStyle(), value);
            }
        }
        return distribution;
    }
    /**
     * Sets the initial value for a specific BotStyle in styleRows.
     * @param style
     * @param value
     */
    public void setInitialValue(BotStyle style, int value) {
        
        for(BotStyleRowController row : styleRows) {
            if(row.getStyle() == style) {
                row.setValue(value);
                break;
            }
        }
    }
    /**
     * Updates the maximum value for each spinner in styleRows based on the remaining number of bots.
     * @param remaining
     */
    public void updateSpinners(int remaining) {

        for(BotStyleRowController row : styleRows) {
            Spinner<Integer> spinner = row.getSpinner();
            int current = spinner.getValue();
            SpinnerValueFactory.IntegerSpinnerValueFactory vf = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
            vf.setMax((current + remaining));
        }
    }
    /**
     * Abstract method to set up the bot card with the provided BotDescriptor, maximum number of bots, and whether bots are allowed.
     * @param bot
     * @param maxBots
     * @param allowBots
     */
    public abstract void setup(BotDescriptor bot, int maxBots, boolean allowBots);
    
    /**
     * Sets up the common elements of the bot card with the provided BotDescriptor.
     * @param bot
     */
    public void setupCommon(BotDescriptor bot) {

        // Update label, description, image and save the bot descriptor to be able to recover it later
        labelBotName.setText( bot.botName() );
        textBotDescription.setText( bot.fullDescription() );
        imgBot.setImage(
            new Image(
                getClass().getResource(bot.iconPath()).toExternalForm()
            )
        );
        cardBackBot.setVisible(false);
        cardFrontBot.setVisible(true);
        _descriptor = bot;
    }

    /**
     * Flips the bot card to show either the front or back side with a rotation animation.
     */
    @FXML
    private void flipCardBot() {
        
        Node visible = isFlippedBot ? cardBackBot : cardFrontBot;
        Node hidden = isFlippedBot ? cardFrontBot : cardBackBot;

        RotateTransition first = new RotateTransition(Duration.millis(350), visible);
        first.setFromAngle(0);
        first.setToAngle(90);
        first.setAxis(Rotate.Y_AXIS);
        first.setInterpolator(javafx.animation.Interpolator.EASE_IN);

        RotateTransition second = new RotateTransition(Duration.millis(200), hidden);
        second.setFromAngle(-90);
        second.setToAngle(0);
        second.setAxis(Rotate.Y_AXIS);
        second.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
         first.setOnFinished(e -> {
            visible.setVisible(false);
            hidden.setVisible(true);
            second.play();
        });

        first.play();
        isFlippedBot = !isFlippedBot;
        
    }

    /**
     * obteins the bot ID from the BotDescriptor.
     * @return Returns the bot ID from the BotDescriptor.
     */
    public int getBotId() { return _descriptor.botId(); }
    /**
     * obteins the BotDescriptor associated with this BotCardController.
     * @return Returns the BotDescriptor associated with this BotCardController.
     */
    public BotDescriptor getDescriptor() { return _descriptor; }
    /**
     * obteins the Runnable that is executed when the value of a spinner changes.
     * @return Returns the Runnable that is executed when the value of a spinner changes.
     */
    protected Runnable getRunnable() { return this.onValueChanged; }
    /**
     * obteins the number of BotStyleRowControllers in styleRows.
     * @return Returns the number of BotStyleRowControllers in styleRows.
     */
    protected int getSpinnerListSize() { return styleRows.size(); }
    /**
     * Sets the Runnable that is executed when the value of a spinner changes.
     * @param r returns the Runnable that is executed when the value of a spinner changes.
     */
    protected void setOnValueChanged(Runnable r) { this.onValueChanged = r; }
}