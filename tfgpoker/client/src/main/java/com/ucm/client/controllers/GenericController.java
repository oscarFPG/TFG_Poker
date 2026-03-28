package com.ucm.client.controllers;

public abstract class GenericController {

    protected MainController mainController;
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
}