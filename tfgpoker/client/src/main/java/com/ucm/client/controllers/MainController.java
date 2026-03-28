package com.ucm.client.controllers;

import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class MainController {
    
    private Stage stage;
    private StatusController statusController;

    public MainController(Stage stage){
        this.stage = stage;
        this.statusController = new StatusController();
    }


    public void start(){
        updateView();
    }


    public void updateView(){
        switch(statusController.getCurrentState()){
            case START_APP:
                loadView("/com/ucm/client/views/original/fxml/profileSecondWindow.fxml");
            break;
            case SET_PROFILE:
                loadView("/com/ucm/client/views/original/fxml/setProfileWindow.fxml");
            break;
        }
    }


    private void loadView(String path){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));

            GenericController controller = loader.getController();
            controller.setMainController(this);

            Scene scene = new Scene(loader.load());
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
}