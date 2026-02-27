package com.ucm.server.gameobjects;

/**
 * Enum representing the different roles a player can have in a poker game.
 * Each role is associated with a unique integer identifier.
 */
public enum PlayerRole {
    
    NO_ROLE(0),
    DEALER(1),
    SMALL_BLIND(2),
    BIG_BLIND(3),
    UNDER_THE_GUN(4),
    MIDDLE_POSITION(5),
    CUT_OFF(6);

    /**
     * The integer identifier for the player role.
     */
    private final int _role;

    /**
     * Constructor for the PlayerRole enum.
     * @param r The integer identifier for the player role.
     */
    PlayerRole(int r) {
        this._role = r;
    }

    /**
     * Returns the integer identifier for the player role.
     * @return player role id
     */
    public int getRoleId() {
        return _role;
    }
}