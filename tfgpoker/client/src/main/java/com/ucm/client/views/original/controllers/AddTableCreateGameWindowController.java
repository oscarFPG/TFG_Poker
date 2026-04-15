package com.ucm.client.views.original.controllers;

import java.util.List;

import com.ucm.client.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AddTableCreateGameWindowController extends GenericController {

    private static final String DEFAULT_TABLE_IMAGE = "/images/tableGame.png";

    @FXML
    private Button btnBackChooseGame;

    @FXML
    private Button btnBackPlayers;

    @FXML
    private Button btnNextCards;

    @FXML
    private Button btnStartAddTables;

    @FXML
    private Button btnNextImageTable;

    @FXML
    private Button btnBackImageTable;

    @FXML
    private ImageView imageTable;

    @FXML
    private Button btnSelectTable;

    private List<String> _images;

    private int _index = 0;

    @Override
    protected void onViewShown() {
        btnStartAddTables.setDisable(true);
        if(_clientInfo.gameConfig._selectedTable == null){
            _clientInfo.gameConfig._selectedTable = DEFAULT_TABLE_IMAGE;
        }
        initializeImage();
    }

    private void initializeImage() {

        _images = List.of(
            "/images/tableGame.png",
            "/images/tableGameBlue.png",
            "/images/tableGameGreen.png",
            "/images/tableGameRed.png"
        );
        if(_clientInfo.gameConfig._selectedTable == null || DEFAULT_TABLE_IMAGE.equals(_clientInfo.gameConfig._selectedTable)) {
            _index = -1;
        }
        else {
            _index = _images.indexOf(_clientInfo.gameConfig._selectedTable);
        }
        showCurrentTable();
    }

    @FXML
    private void nextImage() {
        if(_index == -1) {_index = 0;}
        _index = (_index + 1) % _images.size();
        showCurrentTable();
    }

    @FXML
    private void backImage() {
        if(_index == -1) {_index = 0;}
        _index = (_index - 1 + _images.size()) % _images.size();
        showCurrentTable();
    }

    @FXML
    private void onSelectTable () {
        if(_index == -1) {
            if(DEFAULT_TABLE_IMAGE.equals(_clientInfo.gameConfig._selectedTable)){
                _clientInfo.gameConfig._selectedTable = null;
            }
            else{
                _clientInfo.gameConfig._selectedTable = DEFAULT_TABLE_IMAGE;
            }
        }
        else {
            String currentTableImage = _images.get(_index);
            if(currentTableImage.equals(_clientInfo.gameConfig._selectedTable)) {_clientInfo.gameConfig._selectedTable = null;}
            else {_clientInfo.gameConfig._selectedTable = currentTableImage;}
        }
        updteSelectImage();
    }
    

    private void  showCurrentTable() {
        String currentPath;
        if(_index == -1) {
            currentPath = DEFAULT_TABLE_IMAGE;
        }
        else {
            currentPath = _images.get(_index);
        }
        imageTable.setImage(new Image(getClass().getResource(currentPath).toExternalForm()));
        updteSelectImage();
    }

    private void updteSelectImage() {
        imageTable.getStyleClass().remove("table-selected");
        boolean isSelected;
        if (_index == - 1) {
            isSelected = DEFAULT_TABLE_IMAGE.equals(_clientInfo.gameConfig._selectedTable);
        }
        else {
            isSelected = _clientInfo.gameConfig._selectedTable != null && _images.get(_index).equals(_clientInfo.gameConfig._selectedTable);
        }
        if(isSelected){
            imageTable.getStyleClass().add("table-selected");
            btnSelectTable.setDisable(true);
        }
        else{
            btnSelectTable.setDisable(false);
        }
    }

    @FXML
    public void returnChooseGame() {
        backWindow();
    }

    private void saveAddTable() {
        if(_clientInfo.gameConfig._selectedTable == null) {
            _clientInfo.gameConfig._selectedTable = DEFAULT_TABLE_IMAGE;
        }
    }

    @FXML
    public void jumpToPlayers(){
        back();
    }

    @FXML
    public void jumpToCards(){
        next();
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
