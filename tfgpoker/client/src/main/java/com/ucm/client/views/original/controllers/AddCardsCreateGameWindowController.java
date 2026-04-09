package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.util.List;
import java.net.Socket;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    
    @Override
    protected void onViewShown() {
        btnStartAddCards.setDisable(false);
        if(_clientInfo.gameConfig._selectedCard == null){
            _clientInfo.gameConfig._selectedCard = DEFAULT_CARD_IMAGE;
        }
        initializeImage();
    }

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

    @FXML
    private void nextImageCard() {
        if(_index == -1) {_index = 0;}
        _index = (_index + 1) % _images.size();
        showCurrentTable();
    }

    @FXML
    private void backImageCard() {
        if(_index == -1) {_index = 0;}
        _index = (_index - 1 + _images.size()) % _images.size();
        showCurrentTable();
    }

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
            btnSelectCard.setText("SELECTED");
        }
        else{
            btnSelectCard.setText("SELECT");
        }
    }

    @FXML
    public void returnChooseGame() {
        backWindow();
    }

    private void saveAddTable() {
        if(_clientInfo.gameConfig._selectedCard == null) {
            _clientInfo.gameConfig._selectedCard = DEFAULT_CARD_IMAGE;
        }
    }

    @FXML
    private void sendClientInfo() {

        try {
            PokerPreGame.sendGameConfig(_clientInfo.gameConfig, _clientInfo.socket.getOutputStream());

            int response = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
            if(response == GameType.ERROR_GAME_NOT_CREATED) {
                System.out.printf("Server response: Error creating game!\n");
            }
            else if(response == GameType.CONFIRMATION_WAITING_GAME) {

                _clientInfo.gameConfig._roomId = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());

                System.out.printf("Server response: All correct! Creating room...\n");

                int clientType = SocketUtils.receiveInt(_clientInfo.socket.getInputStream());
                if(clientType == GameType.CONFIRMATION_HOST_PLAYER) {
                    _clientInfo.isHost = true;
                }
                else {
                    System.out.printf("Server response: clientType unknown %d\n", clientType);
                }

                next();
            }
        }
        catch (IOException e) {
            System.out.println( String.format("Error server room name: %s\n", e.getMessage()) );
        }
        catch(NullPointerException e) {
            System.out.println( String.format("Minor problem with socket ONLY for development: %s\n", e.getMessage()) );
        }
    }

    @FXML
    public void jumpToTable(){
        back();
    }

    @Override
    public void onNextEvent() {
        saveAddTable();
    }

    @Override
    public void onBackEvent() {
        saveAddTable();
    }
    
}
