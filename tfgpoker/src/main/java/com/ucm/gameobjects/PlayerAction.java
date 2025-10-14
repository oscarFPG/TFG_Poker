package com.ucm.gameobjects;


public enum PlayerAction {
    FOLD(0), CHECK(1), RAISE(3), FORCED_PLAY(4);
    
    private int _action;
    private int _money;

    PlayerAction(int a){
        _action = a;
        _money = 0;
    }
    
    public void setPot(int p){
        _money = p;
    }

    public int getAction(){ return _action; }
    public int getMoney(){ return _money; }
}
