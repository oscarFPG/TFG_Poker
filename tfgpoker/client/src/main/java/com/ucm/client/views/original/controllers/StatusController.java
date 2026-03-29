package com.ucm.client.views.original.controllers;

public class StatusController {

    public enum State{
        START_APP,
        SET_PROFILE,
        MAIN_WINDOW,
        CHOOSE_GAME,
        CREATE_GAME,
    }
    private State currentState;


    public StatusController() {
        currentState = State.START_APP;
    }


    public State getCurrentState() {
        return currentState;
    }


    public void next(){
        switch(currentState){
            case START_APP:
                currentState = State.SET_PROFILE;
            break;
            case SET_PROFILE:
                currentState = State.MAIN_WINDOW;
            break;
            case MAIN_WINDOW:
                currentState = State.CHOOSE_GAME;
            break;
        }
    }

    public void back(){
        switch(currentState){
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
        }
    }


    public void stateCreateGame(){
        currentState = State.CREATE_GAME;
    }


}
