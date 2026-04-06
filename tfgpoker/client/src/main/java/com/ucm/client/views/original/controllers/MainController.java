package com.ucm.client.views.original.controllers;

import javafx.stage.Stage;
import com.ucm.client.ClientInfo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


public class MainController {
    
    private Stage stage;
    private StatusController statusController;
    private final ClientInfo _clientInfo = ClientInfo.getInstance();
    private static final String path = "/original/fxml/";

    public MainController(Stage stage) {
        this.stage = stage;
        this.statusController = new StatusController();
    }


    public void start() {
        updateView();
    }

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

    private void loadView(String path) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());
            GenericController controller = loader.getController();
            controller.setMainController(this);
            stage.setScene(scene);
            stage.show();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientInfo gClientInfo(){
        return _clientInfo;
    }

    public void next() {
        statusController.next();
    }

    public void back() {
        statusController.back();
    }

    
    public void chooseCreateGame() {
        statusController.stateCreateGame();
        updateView();
    }
}