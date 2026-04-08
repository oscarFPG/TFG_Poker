package com.ucm.client.views.original.controllers;

import java.util.List;

import com.ucm.client.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AddTableCreateGameWindowController extends GenericController {

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

    private List<Image> _images;

    private int _index = 0;

    @Override
    protected void onViewShown() {
        btnStartAddTables.setDisable(true);
        initializeImage();
    }

    private void initializeImage() {
        _images = List.of(
            new Image(getClass().getResource("/images/tableGame.png").toExternalForm()),
            new Image(getClass().getResource("/images/tableGameBlue.png").toExternalForm()),
            new Image(getClass().getResource("/images/tableGameGreen.png").toExternalForm()),
            new Image(getClass().getResource("/images/tableGameRed.png").toExternalForm())
        );
        imageTable.setImage(_images.get(_index));
    }

    @FXML
    private void nextImage() {
        _index = (_index + 1) % _images.size();
        imageTable.setImage(_images.get(_index));
    }

    @FXML
    private void backImage() {
        _index = (_index - 1 + _images.size()) % _images.size();
        imageTable.setImage(_images.get(_index));
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
        
    }

    @Override
    public void onBackEvent() {
        
    }
    
}
