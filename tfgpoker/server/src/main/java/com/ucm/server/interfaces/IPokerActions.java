package com.ucm.server.interfaces;

import com.ucm.server.gameobjects.PlayerRole;


public interface IPokerActions {
    
    public void call(final int amount);
    public void check();
    public void fold();
    public void raise(final int amount);
    public void allIn();

    public int placeOnBetMoney();
    public void unfoldPlayer();
    public void setIsWinner(boolean state);
    public void setIsEliminated(boolean state);
    public void setAllIn(boolean state);

    public int getMoneyOnBet();
    public int getMoneyOffBet();

    public boolean isFolded();
    public boolean isWinner();
    public boolean isAllIn();
    public boolean isEliminated();
    public PlayerRole getRole();

}