package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout.Alignment;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;
import com.ucm.common.GameType;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
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

    @FXML
    private Label labelBotName;         // Bot name
    @FXML
    private VBox cardFrontBot;          // Front card holder
    @FXML
    private StackPane cardBackBot;      // Back card holder
    @FXML
    private StackPane cardInfoBot;      // To display bot info
    @FXML
    private Text textBotDescription;    // Bot description
    @FXML
    private ImageView imgBot;           // Bot image
    @FXML
    private FlowPane flowPaneBotStyles; // Holder for each spinner

    private final List<BotStyleRowController> styleRows = new ArrayList<>();

    private boolean isFlippedBot = false;
    private BotDescriptor _descriptor;
    private Runnable onValueChanged;
    protected boolean _allowStyle;
    

    public BotCardController(final boolean allowStyles) {
        _allowStyle = allowStyles;
    }


    public void addSpinner(BotStyleRowController ctrl) {

        //add a new BotStyleRowController to styleRows
        styleRows.add(ctrl);

        HBox groupStyle = new HBox();
        groupStyle.autosize();
        groupStyle.setSpacing(3); // separación entre Label y Spinner
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

    public int getValue() {
        return styleRows.stream().mapToInt(BotStyleRowController::getValue).sum();
    }

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

    public void setInitialValue(BotStyle style, int value) {
        
        for(BotStyleRowController row : styleRows) {
            if(row.getStyle() == style) {
                row.setValue(value);
                break;
            }
        }
    }

    public void updateSpinners(int remaining) {

        for(BotStyleRowController row : styleRows) {
            Spinner<Integer> spinner = row.getSpinner();
            int current = spinner.getValue();
            SpinnerValueFactory.IntegerSpinnerValueFactory vf = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
            vf.setMax((current + remaining));
        }
    }

    public abstract void setup(BotDescriptor bot, int maxBots, boolean allowBots);
    

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


    public int getBotId() { return _descriptor.botId(); }
    public BotDescriptor getDescriptor() { return _descriptor; }
    protected Runnable getRunnable() { return this.onValueChanged; }
    protected int getSpinnerListSize() { return styleRows.size(); }

    protected void setOnValueChanged(Runnable r) { this.onValueChanged = r; }
}