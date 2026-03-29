package com.ucm.client.views.original.controllers;

import javafx.stage.Stage;

import com.ucm.client.UserProfile;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class MainController {
    
    private Stage stage;
    private StatusController statusController;
    private UserProfile userProfile;

    public MainController(Stage stage){
        this.stage = stage;
        this.statusController = new StatusController();
        this.userProfile = new UserProfile();
    }


    public void start(){
        updateView();
    }


    public void updateView(){
        switch(statusController.getCurrentState()){
            case START_APP:
                loadView("/original/fxml/startWindow.fxml");
            break;
            case SET_PROFILE:
                loadView("/original/fxml/profileSecondWindow.fxml");
            break;
            case MAIN_WINDOW:
                loadView("/original/fxml/mainWindow.fxml");
            break;
        }
    }


    private void loadView(String path){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());
            GenericController controller = loader.getController();
            controller.setMainController(this);
            stage.setScene(scene);
            stage.show();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void next(){
        statusController.next();
        updateView();
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
}