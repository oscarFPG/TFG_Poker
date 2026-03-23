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
    public boolean isAllIn();
    public boolean isEliminated();
    
    /**
     * Retrieve the money that the palyer has bet and sets it to zero.
     * 
     * @return the money retrieved
     */
    public int placeOnBetMoney();
    public void foldPlayer();
    public void unfoldPlayer();
    public void setAllIn(boolean state);
    
    public boolean call(final int amount);
    public boolean check();
    public boolean fold();
    public boolean raise(final int amount);
    public boolean allIn();

}