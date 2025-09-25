package com.ucm.logic;

import com.ucm.gameobjects.Player;

public class PlayerList {
    
    Player[] players;
    int numPlayers;

    public PlayerList(int n){
        numPlayers = n;
        players = new Player[n];
    }

    public void addPlayer(Player p, int pos){
        players[pos] = p;
    }
}