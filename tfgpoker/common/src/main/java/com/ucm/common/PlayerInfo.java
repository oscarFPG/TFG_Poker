package com.ucm.common;

public class PlayerInfo {
    
    public int id;
    public String name;
    public boolean isBot;

    public PlayerInfo(final int ID, final String n, final boolean check) {
        id = ID;
        name = n;
        isBot = check;
    }
}