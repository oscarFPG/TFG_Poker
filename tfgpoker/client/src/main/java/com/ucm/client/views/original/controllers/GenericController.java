package com.ucm.client.views.original.controllers;

import com.ucm.client.ClientInfo;

public abstract class GenericController {

    protected MainController _mainController;
    
    protected ClientInfo _clientInfo;

    public void setMainController(MainController mainController) {
        _mainController = mainController;
        _clientInfo = ClientInfo.getInstance();
        onViewShown();
    }

    public void next() {
        onNextEvent();
        _mainController.next();
        _mainController.updateView();
    }

    public void back() {
        _mainController.back();
        _mainController.updateView();
        onBackEvent();
    }

    public void backWindow() {
        _clientInfo.gameConfig._roomName = _clientInfo.name + "'s room";
        _clientInfo.gameConfig.reset();
        _mainController.backWindow();
        _mainController.updateView();
    }

    protected void chooseCreateGame() {
        _mainController.chooseCreateGame();
    }

    protected void chooseJoinGame() {
        _mainController.chooseJoinGame();
    }
    
    protected void onViewShown() {}

    public abstract void onNextEvent();
    public abstract void onBackEvent();

}