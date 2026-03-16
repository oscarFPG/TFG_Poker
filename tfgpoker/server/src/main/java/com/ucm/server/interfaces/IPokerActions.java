package com.ucm.server.interfaces;

public interface IPokerActions {
    
    public int getMoneyOnBet();
    public int getMoneyOffBet();
    
    public boolean call(final int amount);
    public boolean check();
    public boolean fold();
    public boolean raise(final int amount);
    public boolean allIn();

}