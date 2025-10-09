package com.ucm.gameobjects;


public enum PlayerAction {
    FOLD(0), CHECK(1), RAISE(3);
    
    private int _action;
    private int _pot;

    PlayerAction(int a){
        _action = a;
        _pot = 0;
    }
    
    public void setPot(int p){
        _pot = p;
    }
}
