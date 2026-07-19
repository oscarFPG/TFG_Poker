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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.Node;

public class BotAllowedStyleCardController {

    private static final int MIN_NUM_BOTS = 0;
    private static final int MAX_COLUMN_STYLES = 2;

    @FXML
    private GridPane gridPaneBotStyles;
    @FXML
    private VBox cardFrontBot;
    @FXML
    private StackPane cardInfoBot, cardBackBot;
    @FXML
    private Text textBotDescription;
    @FXML
    private Label labelBotName, labelBotCount;
    @FXML
    private ImageView imgBot;
    @FXML
    private SplitMenuButton splitMenuStyles;

    private boolean isFlippedBot = false;
    private BotDescriptor descriptor;
    private Runnable onValueChanged;
    private final List<BotStyleRowController> styleRows = new ArrayList<>();
    private int _columnStyles = MAX_COLUMN_STYLES;

    public void setup(BotDescriptor bot, int maxBots, boolean allowBots) {
        
        this.descriptor = bot;

        labelBotName.setText(bot.botName());
        textBotDescription.setText(bot.fullDescription());
        imgBot.setImage(new Image(getClass().getResource(bot.iconPath()).toExternalForm()));

        
        for(BotStyle style : BotStyle.values()){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/original/fxml/botStyleRow.fxml"));
                HBox row = loader.load();
                BotStyleRowController controller = loader.getController();
                controller.setup(style, maxBots, allowBots);
                controller.setOnValueChanged(() ->{
                    updateBotCountLabel();
                    if(onValueChanged != null) onValueChanged.run();
                });

                styleRows.add(controller);
                int index = styleRows.size() - 1;
                int rowStyle = index / _columnStyles;
                int column = index % _columnStyles;

                gridPaneBotStyles.add(row, column, rowStyle);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        
        cardBackBot.setVisible(false);
        cardFrontBot.setVisible(true);
    }

    public int getValue() {
        return styleRows.stream().mapToInt(BotStyleRowController::getValue).sum();
    }

    public int getBotId() {
        return descriptor.botId();
    }

    public Map<BotStyle, Integer> getStyleDistribution(){
        Map<BotStyle, Integer> distribution = new HashMap<>();

        for(BotStyleRowController row : styleRows){
            int value = row.getValue();

            if(value > 0) {
                distribution.put(row.getStyle(), value);
            }
        }
        return distribution;
    }

    public void setOnValueChanged(Runnable r) {
        this.onValueChanged = r;
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

    private void updateBotCountLabel() {
        labelBotCount.setText(String.valueOf(getValue()));
    }

    public void updateStyleSpinners(int maxBots){
        for(BotStyleRowController row : styleRows){
            Spinner<Integer> spinner = row.getSpinner();
            int current = spinner.getValue();
            SpinnerValueFactory.IntegerSpinnerValueFactory vf = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
            vf.setMax((current + maxBots));
        }
    }

    public void updateBotCountByType(){
        updateBotCountLabel();
    }
}
