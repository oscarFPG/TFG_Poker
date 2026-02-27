package com.ucm.server.gameobjects;

public enum PlayerRole {
    
    NO_ROLE(0),
    DEALER(1),
    SMALL_BLIND(2),
    BIG_BLIND(3),
    UNDER_THE_GUN(4),
    MIDDLE_POSITION(5),
    CUT_OFF(6);

    private final int _role;

    PlayerRole(int r) {
        this._role = r;
    }

    public int getRoleId() {
        return _role;
    }
}