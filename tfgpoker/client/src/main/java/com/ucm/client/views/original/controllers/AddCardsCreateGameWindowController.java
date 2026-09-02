package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.List;

import com.ucm.client.utils.AlertManager;
import com.ucm.client.utils.Messages;
import com.ucm.client.utils.NotificationManager;
import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/**
 * Controller class for the Add Cards Create Game Window. This class handles the user interactions and logic for selecting card images in the game creation process.
 */
public class AddCardsCreateGameWindowController extends GenericController {
    
    private static final String DEFAULT_CARD_IMAGE = "/images/reversePokerCardGame.png";

    @FXML
    private Button btnBackChooseGame;
    
    @FXML
    private Button btnBackTable;

    @FXML
    private Button btnStartAddCards;

    @FXML
    private Button btnNextImageCard;

    @FXML
    private Button btnBackImageCard;

    @FXML
    private ImageView imageCard;

    @FXML
    private Button btnSelectCard;

    private List<String> _images;

    private int _index = 0;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onViewShown() {
        btnStartAddCards.setDisable(false);
        if(_clientInfo.gameConfig._selectedCard == null){
            _clientInfo.gameConfig._selectedCard = DEFAULT_CARD_IMAGE;
        }
        initializeImage();
    }
    /**
     * Initializes the list of card images and sets the current index based on the selected card in the game configuration.
     * If no card is selected, it defaults to the default card image. Finally, it displays the current card image.
     */
    private void initializeImage() {
        _images = List.of(
            "/images/reversePokerCardGame.png",
            "/images/reversePokerCardBlue.png",
            "/images/reversePokerCardGreen.png",
            "/images/reversePokerCardRed.png"
        );
        if(_clientInfo.gameConfig._selectedCard == null || DEFAULT_CARD_IMAGE.equals(_clientInfo.gameConfig._selectedCard)) {
            _index = -1;
        }
        else {
            _index = _images.indexOf(_clientInfo.gameConfig._selectedCard);
        }
        showCurrentTable();
    }
    /**
     * Navigates to the next card image in the list. If the current index is -1 (indicating no selection), it sets the index to 0. 
     * The index wraps around to the beginning of the list when it reaches the end. After updating the index, it displays the current card image.
     */
    @FXML
    private void nextImageCard() {
        if(_index == -1) {_index = 0;}
        _index = (_index + 1) % _images.size();
        showCurrentTable();
    }
    /**
     * Navigates to the previous card image in the list. If the current index is -1 (indicating no selection), it sets the index to 0.
     */
    @FXML
    private void backImageCard() {
        if(_index == -1) {_index = 0;}
        _index = (_index - 1 + _images.size()) % _images.size();
        showCurrentTable();
    }
    /**
     * Handles the selection of a card image. If the current index is -1, it toggles the selection between the default card image and no selection.
     */
    @FXML
    private void onSelectCard () {
        if(_index == -1) {
            if(DEFAULT_CARD_IMAGE.equals(_clientInfo.gameConfig._selectedCard)){
                _clientInfo.gameConfig._selectedCard = null;
            }
            else{
                _clientInfo.gameConfig._selectedCard = DEFAULT_CARD_IMAGE;
            }
        }
        else {
            String currentCardImage = _images.get(_index);
            if(currentCardImage.equals(_clientInfo.gameConfig._selectedCard)) {_clientInfo.gameConfig._selectedCard = null;}
            else {_clientInfo.gameConfig._selectedCard = currentCardImage;}
        }
        updteSelectImage();
    }
    /**
     * Displays the current card image based on the index. If the index is -1, it shows the default card image. 
     * It also updates the selection state of the card image.
     */
    private void showCurrentTable() {
        String currentPath;
        if(_index == -1) {
            currentPath = DEFAULT_CARD_IMAGE;
        }
        else {
            currentPath = _images.get(_index);
        }
        imageCard.setImage(new Image(getClass().getResource(currentPath).toExternalForm()));
        updteSelectImage();
    }
    /**
     * Updates the selection state of the card image. It checks if the current card image is selected and updates the style and button state accordingly.
     */
    private void updteSelectImage() {
        imageCard.getStyleClass().remove("card-selected");
        boolean isSelected;
        if (_index == - 1) {
            isSelected = DEFAULT_CARD_IMAGE.equals(_clientInfo.gameConfig._selectedCard);
        }
        else {
            isSelected = _clientInfo.gameConfig._selectedCard != null && _images.get(_index).equals(_clientInfo.gameConfig._selectedCard);
        }
        if(isSelected){
            imageCard.getStyleClass().add("card-selected");
            btnSelectCard.setDisable(true);
        }
        else{
            btnSelectCard.setDisable(false);
        }
    }
    /**
     * Navigates back to the Choose Game window. This method is triggered when the user clicks the "Back" button in the Add Cards Create Game Window.
     */
    @FXML
    public void returnChooseGame() {
        backWindow();
    }
    /**
     * Saves the selected card image to the game configuration. If no card is selected, it defaults to the default card image. This method is called when navigating to the next or previous window.
     */
    private void saveAddTable() {
        if(_clientInfo.gameConfig._selectedCard == null) {
            _clientInfo.gameConfig._selectedCard = DEFAULT_CARD_IMAGE;
        }
    }
    /**
     * Calculates the total number of players in the game, including both human players and bots. It sums the number
     * @return the total number of players in the game configuration
     */
    private int totalPlayers() {
        return _clientInfo.gameConfig._numPlayers +  _clientInfo.gameConfig._botsByType.values().stream().mapToInt(Integer::intValue).sum();
    }
    /**
     * Sends the game configuration and client information to the server. It checks if there are enough players to start the game and handles server responses accordingly. If the game is successfully created, it navigates to the next window.
     */
    @FXML
    private void sendClientInfo() {

        boolean canStart = totalPlayers() > 0;

        if(!canStart) {
            AlertManager.show(Messages.Alerts.NOT_ENOUGH_PLAYERS_TITLE, Messages.Alerts.NOT_ENOUGH_PLAYERS_MSG, AlertManager.AlertTypeCustom.WARNING);
        }
        else{
            try {
                PokerPreGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());

                int response = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
                if(response == GameType.ERROR_GAME_NOT_CREATED) {
                    System.out.printf("Server response: Error creating game!\n");
                    NotificationManager.showError(Messages.Notifications.ERROR_GAME_NOT_CREATED);

                }
                else if(response == GameType.CONFIRMATION_WAITING_GAME) {

                    _clientInfo.gameConfig._roomId = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());

                    System.out.printf("Server response: All correct! Creating room...\n");
                    //NotificationManager.showSuccess(Messages.Notifications.CONFIRMATION_WAITING_GAME);

                    int clientType = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
                    if(clientType == GameType.CONFIRMATION_HOST_PLAYER) {
                        _clientInfo.isHost = true;
                    }
                    else {
                        System.out.printf("Server response: clientType unknown %d\n", clientType);
                        NotificationManager.showError(Messages.Notifications.ERROR_CLIENT_TYPE + clientType);
                    }

                    next();
                }
            }
            catch (IOException e) {
                System.out.println( String.format("Error server room name: %s\n", e.getMessage()) );
                NotificationManager.showError(Messages.Notifications.ERROR_SERVER_ROOM_NAME + e.getMessage());
            }
            catch(NullPointerException e) {
                System.out.println( String.format("Minor problem with socket ONLY for development: %s\n", e.getMessage()) );
                NotificationManager.showError(Messages.Notifications.ERROR_SOCKET_DEVELOP + e.getMessage());
            }
        }

        
    }
    /**
     * Navigates to the game table window. 
     * This method is triggered when the user clicks to the next window button in the Add Cards Create Game Window.
     */
    @FXML
    public void jumpToTable(){
        back();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onNextEvent() {
        saveAddTable();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackEvent() {
        saveAddTable();
    }
    
}
