package com.ucm.common;

/**
 * Represents basic information about a player, such as its id and name.
 */
public class PlayerInfo {
    /**
     * The id of the player. This is unique for each player in a game.
     */
    public int id;
    /**
     * The name of the player.
     */
    public String name;

    /**
     * Creates a new PlayerInfo object with the given id and name.
     * @param ID
     * @param n
     */
    public PlayerInfo(final int ID, final String n) {
        id = ID;
        name = n;
    }
}