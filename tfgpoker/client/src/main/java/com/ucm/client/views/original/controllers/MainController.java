package com.ucm.client.views.original.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class that controls the main window of the application. 
 * It manages the different views and transitions between them based on the current state of the application.
 */
public class MainController {
    /**
     * The primary stage of the application where different scenes will be set.
     */
    private Stage _stage;
    /**
     * The controller that manages the current state of the application and determines which view to display.
     */
    private StatusController statusController;
    /**
     * The path to the FXML files that define the different views of the application.
     */
    private static final String path = "/original/fxml/";

    /**
     * Constructor for the MainController class. Initializes the primary stage and the status controller.
     * @param stage
     */
    public MainController(Stage stage) {
        _stage = stage;
        statusController = new StatusController();
    }
    /**
     * Method to start the application by updating the view based on the current state.
     */
    public void start() {
        updateView();
    }
    /**
     * updates the view based on the current state of the application. 
     * It loads the appropriate FXML file and sets it as the scene for the primary stage.
     */
    public void updateView() {

        switch(statusController.getCurrentState()){
        case START_APP:
            loadView(path + "startWindow.fxml");
            break;

        case SET_PROFILE:
            loadView(path + "profileSecondWindow.fxml");
            break;

        case MAIN_WINDOW:
            loadView(path + "mainWindow.fxml");
            break;

        case CHOOSE_GAME:
            loadView(path + "chooseGameWindow.fxml");
            break;

        case CREATE_GAME:
            loadView(path + "homeCreateGameWindow.fxml");
            break;

        case ADD_BOTS:
            loadView(path + "addBotsCreateGameWindow.fxml");
            break;

        case ADD_PLAYERS:
            loadView(path + "addPlayersCreateGameWindow.fxml");
            break;

        case ADD_TABLE:
            loadView(path + "addTableCreateGameWindow.fxml");
            break;

        case ADD_CARDS:
            loadView(path + "addCardsCreateGameWindow.fxml");
            break;

        case WAITING_GAME:
            loadView(path + "waitingGameWindow.fxml");
            break;

        case GAME:
            loadView(path + "inGameWindow.fxml");
            break;
        }
    }
    /**
     * Loads a new view based on the provided FXML file path. 
     * It sets the new scene to the primary stage and initializes the controller for that view.
     * @param path
     */
    private void loadView(String path) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());
            _stage.setScene(scene);
            _stage.setResizable(false);

            GenericController controller = loader.getController();
            controller.setMainController(this, _stage);
            _stage.show();
        } 
        catch (Exception e) {
            System.out.printf("Error trying to load view: %s\n", e.getMessage());
        }
    }
    /**
     * Moves to the next state in the application flow.
     */
    public void next() {
        statusController.next();
    }
    /**
     * Moves back to the previous state in the application flow.
     */
    public void back() {
        statusController.back();
    }
    /**
     * Sets the application state to create a new game and updates the view accordingly.
     */
    public void chooseCreateGame() {
        statusController.stateCreateGame();
        updateView();
    }
    /**
     * Sets the application state to join an existing game and updates the view accordingly.
     */
    public void chooseJoinGame() {
        statusController.stateJoinGame();
        updateView();
    }
    /**
     * Sets the application state to choose a game and updates the view accordingly.
     */
    public void backWindow() {
        statusController.stateChooseGame();
        updateView();
    }
}