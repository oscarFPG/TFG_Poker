package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.ucm.common.BotDescriptor;
import com.ucm.common.BotStyle;
import com.ucm.common.GameType;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;

public class BotCardController {

    private static final int MIN_NUM_BOTS = 0;

    @FXML
    private Spinner<Integer> spinnerBot;
    @FXML
    private StackPane cardInfoBot;
    @FXML
    private VBox cardFrontBot, vBoxBotStyles;
    @FXML
    private StackPane cardBackBot;
    @FXML
    private Text textBotDescription;
    @FXML
    private Label labelBotName;
    @FXML
    private ImageView imgBot;
    @FXML
    private SplitMenuButton splitMenuStyles;

    private boolean isFlippedBot = false;
    private BotDescriptor descriptor;
    private Runnable onValueChanged;
    private final List<BotStyleRowController> styleRows = new ArrayList<>();

    public void setup(BotDescriptor bot, int maxBots, boolean allowBots) {
        
        this.descriptor = bot;

        labelBotName.setText(bot.botName());
        textBotDescription.setText(bot.fullDescription());
        imgBot.setImage(new Image(getClass().getResource(bot.iconPath()).toExternalForm()));

        spinnerBot.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_BOTS, maxBots, 0));
        spinnerBot.setDisable(!allowBots);
        spinnerBot.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateStyleSpinnerCount();
            updateBotStyleControls();
            updateStyleSpinners();
            if(onValueChanged != null) onValueChanged.run();
            
        });

        if(bot.allowStyles()){
            for(BotStyle style : BotStyle.values()){
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/original/fxml/botStyleRow.fxml"));
                    HBox row = loader.load();
                    BotStyleRowController controller = loader.getController();
                    controller.setup(style, maxBots);
                    controller.setOnValueChanged(this::updateStyleSpinners);
                    styleRows.add(controller);
                    vBoxBotStyles.getChildren().add(row);
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        updateBotStyleControls();
        updateStyleSpinners();
        
        cardBackBot.setVisible(false);
        cardFrontBot.setVisible(true);
    }

    public int getValue() {
        return spinnerBot.getValue();
    }

    public int getBotId() {
        return descriptor.botId();
    }

    public Spinner<Integer> getSpinner() {
        return spinnerBot;
    }

    public Map<BotStyle, Integer> getStyleDistribution(){
        Map<BotStyle, Integer> distribution = new HashMap<>();

        int assignedStyles = 0;

        for(BotStyleRowController row : styleRows) {
            int value = row.getValue();

            if(value > 0){
                distribution.put(row.getStyle(), value);
            }

            assignedStyles += value;
        }

        int remainingBots = spinnerBot.getValue() - assignedStyles;

        if(remainingBots > 0){
            distribution.merge(BotStyle.DEFAULT, remainingBots, Integer::sum);
        }

        return distribution;
    }

    public void setOnValueChanged(Runnable r) {
        this.onValueChanged = r;
    }

    public void setInitialValue(int value) {
        spinnerBot.getValueFactory().setValue(value);
    }

    public void setInitialStylesValue(BotStyle style, int value){
        for(BotStyleRowController row : styleRows){
            if(row.getStyle() == style) {
                row.setValue(value);
                break;
            }
        }
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

    private void updateStyleSpinners(){
        int totalBotStyles = styleRows.stream().mapToInt(BotStyleRowController::getValue).sum();
        int remaining = Math.max(0, spinnerBot.getValue() - totalBotStyles);
        for(BotStyleRowController row : styleRows){
            Spinner<Integer> spinner = row.getSpinner();
            int current = spinner.getValue();
            SpinnerValueFactory.IntegerSpinnerValueFactory vf = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
            vf.setMax((current + remaining));
        }
    }

    private void updateBotStyleControls() {
        boolean enabled = spinnerBot.getValue() > 0;
        vBoxBotStyles.setDisable(!enabled);
        if(!enabled) {
            styleRows.forEach(row -> {row.setValue(0);});
            vBoxBotStyles.setVisible(false);
            vBoxBotStyles.setManaged(false);
        }
        else{
            vBoxBotStyles.setVisible(true);
            vBoxBotStyles.setManaged(true);
        }
    }

    private void updateStyleSpinnerCount(){
        int botCount = spinnerBot.getValue();
        int styleCount = styleRows.stream().mapToInt(BotStyleRowController::getValue).sum();
        int remaining = botCount - styleCount;
        if((remaining) == 0) return;
        for(BotStyleRowController row : styleRows){
            if(row.getStyle() == BotStyle.DEFAULT){
                row.setValue(Math.max(0, row.getValue() + remaining));
                break;
            }
        }
    }
}
