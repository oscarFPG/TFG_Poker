package com.ucm.gameobjects;

public enum PlayerRole {
    
    NO_ROLE(0), DEALER(1), SMALL_BLIND(2), BIG_BLIND(3);
    
    int _role;

    PlayerRole(int r){
        _role = r;
    }
}