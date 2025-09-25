package com.ucm.logic;

import com.ucm.gameobjects.Player;

public class PlayerList {
    
    Player[] players;
    int _contPlayers;

    public PlayerList(){
        _contPlayers = 0;
        players = new Player[Game.NUM_MAX_PLAYERS];
    }

    public PlayerList(int n){
        _contPlayers = 0;
        players = new Player[n];
    }

    public int getContPlayers(){ return _contPlayers; }

    public void addPlayer(Player p){
        players[_contPlayers] = p;
        _contPlayers++;
    }
}