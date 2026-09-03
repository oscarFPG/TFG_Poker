package com.ucm.client.views.original.controllers;

/**
 * Controller that manages the current state of the application. 
 */
public class StatusController {

    /**
     * Enum that represents the different states of the application.
     */
    public enum State {
        START_APP,
        SET_PROFILE,
        MAIN_WINDOW,
        CHOOSE_GAME,
        CREATE_GAME,
        ADD_BOTS,
        ADD_PLAYERS,
        ADD_TABLE,
        ADD_CARDS,
        WAITING_GAME,
        GAME
    }
    /**
     * The current state of the application.
     */
    private State currentState;
    /**
     * Constructor for the StatusController class. Initializes the current state to START_APP.
     */
    public StatusController() {
        currentState = State.START_APP;
    }
    /**
     * Method to transition to the next state based on the current state of the application.
     */
    public void next() {

        switch(currentState) {
        case START_APP:
            currentState = State.SET_PROFILE;
            break;

        case SET_PROFILE:
            currentState = State.MAIN_WINDOW;
            break;

        case MAIN_WINDOW:
            currentState = State.CHOOSE_GAME;
            break;

        case CREATE_GAME:
            currentState = State.ADD_BOTS;
            break;

        case ADD_BOTS:
            currentState = State.ADD_PLAYERS;
            break;

        case ADD_PLAYERS:
            currentState = State.ADD_TABLE;
            break;

        case ADD_TABLE:
            currentState = State.ADD_CARDS;
            break;

        case ADD_CARDS:
            currentState = State.WAITING_GAME;
            break;

        case WAITING_GAME:
            currentState = State.GAME;
            break;

        case GAME:
            currentState = State.START_APP;
            break;

        default:
            currentState = State.START_APP;
            break;
        }
        
    }
    /**
     * Method to transition to the previous state based on the current state of the application.
     */
    public void back() {

        switch(currentState) {
        case SET_PROFILE:
            currentState = State.START_APP;
            break;

        case MAIN_WINDOW:
            currentState = State.SET_PROFILE;
            break;

        case CHOOSE_GAME:
            currentState = State.MAIN_WINDOW;
            break;

        case CREATE_GAME:
            currentState = State.CHOOSE_GAME;
            break;

        case ADD_BOTS:
            currentState = State.CREATE_GAME;
            break;

        case ADD_PLAYERS:
            currentState = State.ADD_BOTS;
            break;

        case ADD_TABLE:
            currentState = State.ADD_PLAYERS;
            break;

        case ADD_CARDS:
            currentState = State.ADD_TABLE;
            break;

        case WAITING_GAME:
            currentState = State.CHOOSE_GAME;
            break;

        case GAME:
            currentState = State.START_APP;
            break;
        
        default:
            currentState = State.START_APP;
            break;
        }
    }
    /**
     * Method to set the current state to CREATE_GAME.
     */
    public void stateCreateGame() {
        currentState = State.CREATE_GAME;
    }
    /**
     * Method to set the current state to WAITING_GAME.
     */
    public void stateJoinGame() {
        currentState = State.WAITING_GAME;
    }
    /**
     * Method to set the current state to CHOOSE_GAME.
     */
    public void stateChooseGame() {
        currentState = State.CHOOSE_GAME;
    }
    /**
     * Method to get the current state of the application.
     * @return The current state of the application.
     */
    public State getCurrentState() {
        return currentState;
    }


}
