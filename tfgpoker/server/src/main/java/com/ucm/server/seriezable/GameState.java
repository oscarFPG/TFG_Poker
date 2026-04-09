package com.ucm.server.seriezable;

import java.io.Serializable;
import java.util.List;

import com.ucm.common.gameobjects.Card;
import com.ucm.server.logic.PlayerList;


//SOLO UNA IDEA, NO CONCLUIDA
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<PlayerList> players;

  
    private Card[] tableCards;
    private int actualTableCards;

    
    private int totalPot;
    private int handCounter;
    private boolean isPreflop;
    private boolean showdownSkipped;

    private int currentSB;
    private int currentBB;

    public GameState(
            List<PlayerList> players,
            Card[] tableCards,
            int actualTableCards,
            int totalPot,
            int handCounter,
            boolean isPreflop,
            boolean showdownSkipped,
            int currentSB,
            int currentBB) {

        this.players = players;
        this.tableCards = tableCards;
        this.actualTableCards = actualTableCards;
        this.totalPot = totalPot;
        this.handCounter = handCounter;
        this.isPreflop = isPreflop;
        this.showdownSkipped = showdownSkipped;
        this.currentSB = currentSB;
        this.currentBB = currentBB;
    }

    
}