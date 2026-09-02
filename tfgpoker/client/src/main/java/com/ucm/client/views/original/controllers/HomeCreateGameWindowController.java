package com.ucm.client.views.original.controllers;


import com.ucm.common.GameConfig;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 * Controller for the Home Create Game Window.
 */
public class HomeCreateGameWindowController extends GenericController {

    private static final int MAX_ROOM_NAME_LENGTH = 17;
    private static final int MIN_INITIAL_MONEY = 100;
    private static final int MAX_INITIAL_MONEY = 1000000;
    private static final String DEFAULT_LEVEL_DURATION = "15";
    private static final String DEFAULT_HIKE_PERCENTAGE = "25";
    
    /**
     * Button to go back to the Choose Game window.
     */
    @FXML
    private Button btnBackChooseGame;
    /**
     * Button to proceed to the next view (Bots configuration).
     */
    @FXML
    private Button btnNextHome;
    /**
     * Button to start the game that has been created, 
     * only works if the configuration of the game is correct, if not the botton is disable.
     */
    @FXML
    private Button btnStartHome;
    /**
     * Text field to enter the name of the room.
     */
    @FXML
    private TextField textFieldRoomName;
    /**
     * Label to display the name of the user who is creating the game.
     */
    @FXML
    private Label labelUserName;
    /**
     * Spinner to select the initial money for the game.
     */
    @FXML
    private Spinner<Integer> spinnerInitialMoney;
    /**
     * CheckBox to allow or disallow bots in the game.
     */
    @FXML
    private CheckBox checkBoxAllowBots, checkBoxSpectator;
    /**
     * ComboBox to select the blinds value for the game.
     */
    @FXML
    private ComboBox<String> comboBlindsValue;
    /**
     * Button to enable or disable dynamic blinds for the game.
     */
    @FXML
    private Button btnDinamicBlinds;
    /**
     * ComboBox to select the level duration for the game.
     */
    @FXML
    private ComboBox<String> comboLevelDuration;
    /**
     * ComboBox to select the hike percentage for the game.
     */
    @FXML
    private ComboBox<String> comboHikePercentage;
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onViewShown() {

        btnStartHome.setDisable(true);
        labelUserName.setText(_clientInfo.name);
        checkBoxAllowBots.setSelected(_clientInfo.gameConfig._allowBots);
        checkBoxSpectator.setSelected(_clientInfo.gameConfig._joinedAsSpectator);

        initializeRoomName();
        initializeSpinner();
        initializeBlindsValue();
        initializeLevelDuration();
        initializeHikePercentage();
        updateDinamicBlinds();
    }
    /**
     * Initializes the room name text field with the current room name from the game configuration.
     * If the current room name is invalid, it sets a default room name based on the user's name.
     * It also sets a text formatter to limit the length and allowed characters for the room name.
     */
    private void initializeRoomName() {
        String roomName = _clientInfo.gameConfig._roomName;
        if(!GameConfig.isValidRoomName(roomName)) {
            roomName = _clientInfo.name + "'s room";
            _clientInfo.gameConfig._roomName = roomName;
        }
        textFieldRoomName.setText(roomName);
        textFieldRoomName.selectAll();
        textFieldRoomName.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > MAX_ROOM_NAME_LENGTH) {
                return null;
            }
            if (!newText.matches("[a-zA-Z0-9 ]*")) {
                return null;
            }
            return change;
        }));
    }
    /**
     * Initializes the blinds value combo box with predefined blinds options.
     */
    private void initializeBlindsValue() {

        comboBlindsValue.getItems().clear();

        final int[] blinds = {1, 5, 10, 25, 50, 100};
        for (int blind : blinds) {
            comboBlindsValue.getItems().add(blind + "/" + (blind * 2));
        }

        String restoreBlindsValue = _clientInfo.gameConfig._blindsValue;
        if( restoreBlindsValue != null) {
            comboBlindsValue.setValue(restoreBlindsValue);
        }
    }
    /**
     * Initializes the level duration combo box with predefined level duration options.
     */
    private void initializeLevelDuration() {

        comboLevelDuration.getItems().clear();

        final int[] durations = {5, 10, 15, 20, 30};
        for(int dur : durations) {
            comboLevelDuration.getItems().add( String.valueOf(dur) );
        }

        String restoreLevelDuration = _clientInfo.gameConfig._levelDuration;
        if(restoreLevelDuration != null && !comboLevelDuration.isDisable()) {
            comboLevelDuration.setValue(restoreLevelDuration);
        }
    }
    /**
     * Initializes the hike percentage combo box with predefined hike percentage options.
     */
    private void initializeHikePercentage() {

        comboHikePercentage.getItems().clear();

        final int[] hikes = {25, 33, 50, 67, 80, 100};
        for(int h : hikes) {
            comboHikePercentage.getItems().add( String.valueOf(h) );
        }

        String restorehikePercentage = _clientInfo.gameConfig._hikePercentage;
        if( restorehikePercentage != null && !comboHikePercentage.isDisable()) {
            comboHikePercentage.setValue(restorehikePercentage);
        }
    }
    /**
     * Initializes the initial money spinner with a value factory and a text formatter.
     * The spinner allows the user to select an initial money value within a defined range.
     * The text formatter ensures that only valid integer values are accepted.
     */
    private void initializeSpinner() {

        int initialValue = _clientInfo.gameConfig._initialMoney;
        if(initialValue < MIN_INITIAL_MONEY || initialValue > MAX_INITIAL_MONEY) {
            initialValue = MIN_INITIAL_MONEY;
        }
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_INITIAL_MONEY, MAX_INITIAL_MONEY, initialValue);
        spinnerInitialMoney.setValueFactory(valueFactory);
        spinnerInitialMoney.setEditable(true);
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), initialValue, change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        });
        spinnerInitialMoney.getEditor().setTextFormatter(formatter);
        formatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            int clampedValue = Math.clamp(newValue, MIN_INITIAL_MONEY, MAX_INITIAL_MONEY);
            valueFactory.setValue(clampedValue);
        });
    }
    /**
     * Toggles the dynamic blinds setting in the game configuration and updates the UI accordingly.
     * When dynamic blinds are enabled, the level duration and hike percentage combo boxes are enabled.
     * When disabled, they are set to default values and disabled.
     */
    @FXML
    public void onDinamicBlinds () {
        boolean dinamicBlindsEnabled = !_clientInfo.gameConfig._dynamicBlinds;
        _clientInfo.gameConfig._dynamicBlinds = dinamicBlindsEnabled;
        updateDinamicBlinds();
    }
    /**
     * Updates the UI elements related to dynamic blinds based on the current setting in the game configuration.
     * If dynamic blinds are enabled, the level duration and hike percentage combo boxes are enabled.
     * If disabled, they are set to default values and disabled.
     */
    private void updateDinamicBlinds() {

        boolean enabled = _clientInfo.gameConfig._dynamicBlinds;
        if(!enabled) {
            comboLevelDuration.setValue(DEFAULT_LEVEL_DURATION);
            comboHikePercentage.setValue(DEFAULT_HIKE_PERCENTAGE);
        }
        comboLevelDuration.setDisable(!enabled);
        comboHikePercentage.setDisable(!enabled);

        btnDinamicBlinds.setText(enabled ? "yes" : "no");
    }
    /**
     * Updates the game configuration to allow or disallow bots based on the state of the corresponding checkbox.
     */
    @FXML
    private void onAllowBots() {
        _clientInfo.gameConfig._allowBots = checkBoxAllowBots.isSelected();
    }
    /**
     * Updates the game configuration to allow or disallow spectators based on the state of the corresponding checkbox.
     */
    @FXML
    private void onJoinAsSpectator(){
        _clientInfo.gameConfig._joinedAsSpectator = checkBoxSpectator.isSelected();
    }
    /**
     * Navigates back to the Choose Game window.
     */
    @FXML
    public void returnChooseGame() {
        backWindow();
    }
    /**
     * Navigates to the Bots configuration window.
     */
    @FXML
    public void jumpToBots() {
        next();
    }
    /**
     * Saves the current configuration of the home game to the client information.
     * It validates the room name and sets default values for any missing or invalid configurations.
     */
    private void saveHomeConfig() {
        
        String roomName = textFieldRoomName.getText();
        String userName = labelUserName.getText();
        Integer initialMoney = spinnerInitialMoney.getValue();
        String blindsValue = comboBlindsValue.getValue();
        String levelDuration = comboLevelDuration.getValue();
        String hikePercentage = comboHikePercentage.getValue();

        boolean validRoomName = GameConfig.isValidRoomName(roomName);
        if(roomName == null || !validRoomName) {
            roomName = _clientInfo.name + "'s room";
        }
        if (blindsValue == null || blindsValue.isEmpty()) {
            blindsValue = _clientInfo.gameConfig._blindsValue;
        }
        if (levelDuration == null || levelDuration.isEmpty()) {
            levelDuration = _clientInfo.gameConfig._levelDuration;
        }
        if (hikePercentage == null || hikePercentage.isEmpty()) {
            hikePercentage = _clientInfo.gameConfig._hikePercentage;
        }
        _clientInfo.gameConfig._roomName = roomName;
        _clientInfo.gameConfig._userName = userName;
        _clientInfo.gameConfig._initialMoney = initialMoney;
        _clientInfo.gameConfig._blindsValue = blindsValue;
        _clientInfo.gameConfig._levelDuration = levelDuration;
        _clientInfo.gameConfig._hikePercentage = hikePercentage;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onNextEvent() {
        saveHomeConfig();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackEvent() {
        saveHomeConfig();
    }

}