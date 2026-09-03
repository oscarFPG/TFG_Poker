package com.ucm.client.views.original.controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the Add Table Create Game Window. This class manages the user interface and interactions related to selecting a table image for the game configuration. 
 * It allows users to navigate through available table images, select a preferred table, and save their selection. The controller also handles navigation between different stages of the game setup process.
 */
public class AddTableCreateGameWindowController extends GenericController {

    private static final String DEFAULT_TABLE_IMAGE = "/images/tableGame.png";
    /**
     * FXML-injected button for navigating back to the game selection window. This button allows users to return to the previous stage of the game setup process.
     */
    @FXML
    private Button btnBackChooseGame;
    /**
     * FXML-injected button for navigating back to the players selection window. This button allows users to return to the previous stage of the game setup process.
     */
    @FXML
    private Button btnBackPlayers;
    /**
     * FXML-injected button for navigating to the next window in the game setup process, which is the cards selection window. This button allows users to proceed to the next stage of configuration.
     */
    @FXML
    private Button btnNextCards;
    /**
     * FXML-injected button for starting the process of adding tables. This button is disabled by default and is enabled when a table image is selected, allowing users to proceed with their selection.
     */
    @FXML
    private Button btnStartAddTables;
    /**
     * FXML-injected button for navigating to the next table image in the list. This button allows users to cycle through available table images and select their preferred option.
     */
    @FXML
    private Button btnNextImageTable;
    /**
     * FXML-injected button for navigating to the previous table image in the list. This button allows users to cycle through available table images in reverse order and select their preferred option.
     */
    @FXML
    private Button btnBackImageTable;
    /**
     * FXML-injected ImageView for displaying the currently selected table image. This component updates to show the image corresponding to the current index in the list of available table images.
     */
    @FXML
    private ImageView imageTable;
    /**
     * FXML-injected button for selecting the currently displayed table image. This button allows users to confirm their selection of a table image, which is then saved to the game configuration.
     */
    @FXML
    private Button btnSelectTable;

    private List<String> _images;

    private int _index = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onViewShown() {
        btnStartAddTables.setDisable(true);
        if(_clientInfo.gameConfig._selectedTable == null){
            _clientInfo.gameConfig._selectedTable = DEFAULT_TABLE_IMAGE;
        }
        initializeImage();
    }
    /**
     * Initializes the list of available table images and sets the current index based on the selected table in the game configuration. If no table is selected, it defaults to the first image. The method also updates the displayed image accordingly.
     */
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
    /**
     * Navigates to the next table image in the list. If the current index is -1 (indicating no selection), it sets the index to 0. The method wraps around to the beginning of the list when reaching the end, ensuring continuous navigation through available images. 
     * After updating the index, it calls `showCurrentTable()` to display the selected image.
     */
    @FXML
    private void nextImage() {
        if(_index == -1) {_index = 0;}
        _index = (_index + 1) % _images.size();
        showCurrentTable();
    }
    /**
     * Navigates to the previous table image in the list. If the current index is -1 (indicating no selection), it sets the index to 0. 
     * The method wraps around to the end of the list when reaching the beginning, ensuring continuous navigation through available images.
     */
    @FXML
    private void backImage() {
        if(_index == -1) {_index = 0;}
        _index = (_index - 1 + _images.size()) % _images.size();
        showCurrentTable();
    }
    /**
     * Handles the selection of a table image. If no image is currently selected (indicated by an index of -1), it toggles the selection between the default table image and no selection.
     */
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
    /**
     * Displays the current table image based on the selected index. If no image is selected (index -1), it defaults to the predefined default table image. The method updates the `ImageView` component to show the appropriate image and calls `updteSelectImage()` to reflect the selection state in the user interface.
     */
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
    /**
     * Updates the selection state of the table image in the user interface. It checks if the currently displayed image matches the selected table in the game configuration. 
     * If it does, it adds a "table-selected" style class to the `ImageView` and disables the selection button. 
     * If not, it removes the style class and enables the selection button, allowing users to select a different table image.
     */
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
    /**
     * Navigates back to the previous window in the game setup process, allowing users to return to the game selection stage. 
     * This method is triggered by a user action, such as clicking a "<" button.
     */
    @FXML
    public void returnChooseGame() {
        backWindow();
    }
    /**
     * Saves the selected table image to the game configuration. 
     * If no table image is selected, it defaults to the predefined default table image.
     * This method is called when navigating to the next or previous window in the game setup process to ensure that the user's selection is preserved.
     */
    private void saveAddTable() {
        if(_clientInfo.gameConfig._selectedTable == null) {
            _clientInfo.gameConfig._selectedTable = DEFAULT_TABLE_IMAGE;
        }
    }
    /**
     * Navigates back to the players selection window in the game setup process. 
     * This method is triggered by a user action, such as clicking a "<" button, 
     * allowing users to return to the previous stage of configuration.
     */
    @FXML
    public void jumpToPlayers(){
        back();
    }
    /**
     * Navigates to the cards selection window in the game setup process.
     * This method is triggered by a user action, such as clicking a ">" button,
     */
    @FXML
    public void jumpToCards(){
        next();
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
