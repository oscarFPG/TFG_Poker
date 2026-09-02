package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;


public class AddPlayersCreateGameWindowController extends GenericController  {

    private static final int MIN_NUM_PLAYERS = 0;
    private static final int MAX_NUM_PLAYERS = 8;
    private static final int MAX_NUM_PLAYERS_SPECTATOR = 9;

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryPlayer;

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnBackBots;

    @FXML
    private Button btnNextTable;

    @FXML
    private Button btnStartAddPlayers;

    @FXML
    private Label labelRemainingPlayers;

    @FXML
    private Spinner<Integer> spinnerAddPlayers;
    
    @FXML
    private ComboBox<String> comboTurnTimerPlayer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onViewShown() {
        btnStartAddPlayers.setDisable(true);
        initializeSpinnner();
        initializeRemainingPlayers();
        initializeTurnTimerPlayer();
    }
    /**
     * Initializes the spinner for the number of players, setting its minimum, maximum, and initial values based on the current game configuration and whether the user is a spectator. 
     * It also adds a listener to update the remaining players label whenever the spinner value changes.
     */
    private void initializeSpinnner() {
        int maxPlayers = getMaxRemainingPlayers();
        int initialNumPlayers = isSpectator() ? _clientInfo.gameConfig._numPlayers + 1 : _clientInfo.gameConfig._numPlayers;
        if(initialNumPlayers > maxPlayers) {
            initialNumPlayers = maxPlayers;
        }
        valueFactoryPlayer = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NUM_PLAYERS, maxPlayers, initialNumPlayers);
        spinnerAddPlayers.setValueFactory(valueFactoryPlayer);
        spinnerAddPlayers.valueProperty().addListener((obs, oldValue, newValue) -> initializeRemainingPlayers());
    }
    /**
     * Updates the label showing the number of remaining players that can be added to the game. It calculates the remaining players by subtracting the current number of players selected in the spinner from the maximum allowed players, and updates the label accordingly.
     */
    private void initializeRemainingPlayers() {
        int maxPlayers = getMaxRemainingPlayers();
        int numPlayers = spinnerAddPlayers.getValue();
        int remainingPlayers = maxPlayers - numPlayers;
        labelRemainingPlayers.setText(String.valueOf(remainingPlayers));
    }
    /**
     * Initializes the combo box for selecting the turn timer for players. It populates the combo box with predefined timer values (60, 180, 360, 720 seconds) and restores the previously selected value from the game configuration if available and if the combo box is not disabled.
     */
    private void initializeTurnTimerPlayer() {
        comboTurnTimerPlayer.getItems().addAll(
            "60",
            "180",
            "360",
            "720"
        );
        String restoreTurnTimerPlayer = _clientInfo.gameConfig._turnTimerPlayer;
        if(restoreTurnTimerPlayer != null && !comboTurnTimerPlayer.isDisable()) {
            comboTurnTimerPlayer.setValue(restoreTurnTimerPlayer);
        }
    }
    /**
     * Calculates the maximum number of remaining players that can be added to the game based on the current game configuration and whether the user is a spectator. 
     * It takes into account the total number of bots already configured in the game and adjusts the maximum allowed players accordingly.
     * @return the maximum number of remaining players that can be added to the game
     */
    private int getMaxRemainingPlayers() {
        int totalBots = _clientInfo.gameConfig._botsByType.values().stream().mapToInt(Integer::intValue).sum();
        if(isSpectator ()){
            return MAX_NUM_PLAYERS_SPECTATOR - totalBots;
        }
        return MAX_NUM_PLAYERS - totalBots;
    }
    /**
     * Saves the current configuration of the number of players and the turn timer for players to the game configuration. It retrieves the values from the spinner and combo box, and updates the corresponding fields in the game configuration object. If the turn timer value is not set, it retains the previous value from the game configuration.
     */
    private void saveAddPlayers() {
        _clientInfo.gameConfig._numPlayers = spinnerAddPlayers.getValue();
        String timerTurn = comboTurnTimerPlayer.getValue();

        if(timerTurn == null || timerTurn.isEmpty()) {
            timerTurn = _clientInfo.gameConfig._turnTimerPlayer;
        }

        _clientInfo.gameConfig._turnTimerPlayer = timerTurn;
    }
    /**
     * Checks if the current user has joined the game as a spectator. It returns true if the user is a spectator, and false otherwise.
     * @return true if the user is a spectator, false otherwise
     */
    private boolean isSpectator () {
        return _clientInfo.gameConfig._joinedAsSpectator;
    }
    /**
     * Navigates back to the previous window where the user can choose a game. 
     * It calls the backWindow() method to handle the navigation.
     */
    @FXML
    public void returnChooseGame() {
        backWindow();
    }
    /**
     * Navigates back to the previous window where the user can add bots. It calls the back() method to handle the navigation.
     */
    @FXML
    public void jumpToBots(){
        back();
    }
    /**
     * Navigates to the next window where the user can view the game table. It calls the next() method to handle the navigation.
     */
    @FXML
    public void jumpToTable(){
        next();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onNextEvent() {
      saveAddPlayers();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackEvent() {
       saveAddPlayers();
    }

}
