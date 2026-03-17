package com.ucm.server.interfaces;

import com.ucm.server.gameobjects.Card;

public interface IPokerActions {
    
    public String getPlayerName();
    public int getMoneyOnBet();
    public int getMoneyOffBet();
    public int getCardsCounter();
    public Card[] getPlayerCards();

    public boolean isFolded();
    public boolean isWinner();
    
    public void foldPlayer();
    public void unfoldPlayer();
    
    public boolean call(final int amount);
    public boolean check();
    public boolean fold();
    public boolean raise(final int amount);
    public boolean allIn();

}