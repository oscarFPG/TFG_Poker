package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

import javafx.stage.Stage;

/**
 * GenericController is an abstract class that serves as a base for all controllers in the application.
 * It provides common functionality for managing the main controller, stage, and client information.
 */
public abstract class GenericController {

    /**
     * The main controller that manages the overall flow of the application.
     */
    protected MainController _mainController;
    /**
     * The stage representing the primary window of the application.
     */
    protected Stage _stage;
    /**
     * The client information that holds the state and configuration of the client.
     */
    protected ClientInfo _clientInfo;

    /**
     * Sets the main controller and stage for this controller.
     * @param mainController
     * @param stage
     */
    public void setMainController(MainController mainController, Stage stage) {
        _mainController = mainController;
        _stage = stage;
        _clientInfo = ClientInfo.getInstance();
        onViewShown();
    }
    /**
     * Triggers the next event in the application flow, updates the view, and calls the onNextEvent method for additional handling.
     */
    public void next() {
        onNextEvent();
        _mainController.next();
        _mainController.updateView();
    }
    /**
     * Triggers the back event in the application flow, updates the view, and calls the onBackEvent method for additional handling.
     */
    public void back() {
        _mainController.back();
        _mainController.updateView();
        onBackEvent();
    }
    /**
     * Handles the action of going back to the previous window. It resets the game configuration and updates the view accordingly.
     */
    public void backWindow() {
        _clientInfo.gameConfig._roomName = _clientInfo.name + "'s room";
        _clientInfo.gameConfig.reset();
        _mainController.backWindow();
        _mainController.updateView();
    }
    /**
     * Handles the action of choosing to create a new game.
     */
    protected void chooseCreateGame() {
        _mainController.chooseCreateGame();
    }
    /**
     * Handles the action of choosing to join an existing game.
     */
    protected void chooseJoinGame() {
        _mainController.chooseJoinGame();
    }
    /**
     * This method is called when the view associated with this controller is shown. It can be overridden by subclasses to perform specific actions when the view is displayed.
     */
    protected void onViewShown() {}
    /**
     * This method is called when the next event is triggered. It can be overridden by subclasses to perform specific actions when the next event occurs.
     */
    protected void onNextEvent(){}
    /**
     * This method is called when the back event is triggered. It can be overridden by subclasses to perform specific actions when the back event occurs.
     */
    protected void onBackEvent() {}

}