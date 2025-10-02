package com.ucm.logic;

import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Card;

public class PlayerList {

    private Player[] players;
    private int _contPlayers;

    public PlayerList() {
        _contPlayers = 0;
        players = new Player[Game.NUM_MAX_PLAYERS];
    }

    public PlayerList(int n) {
        _contPlayers = 0;
        players = new Player[n];
    }

    public int getContPlayers() {
        return _contPlayers;
    }

    public void addPlayer(Player p) {
        players[_contPlayers] = p;
        _contPlayers++;
    }

    public void retrieveAllCardsFromPlayers() {
        for (Player p : players) {
            for (Card c : p.getCards()) {
                c.setAvailable(true);
            }
        }
    }

    public void retrieveAllCardsFromPlayer(Player p) {
        for (Card c : p.getCards()) {
            c.setAvailable(true);
        }
    }
}