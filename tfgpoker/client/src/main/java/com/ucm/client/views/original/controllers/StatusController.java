package com.ucm.client.views.original.controllers;

public class StatusController {

    public enum State{
        START_APP,
        SET_PROFILE,
        MAIN_WINDOW,
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
        }
    }


}
