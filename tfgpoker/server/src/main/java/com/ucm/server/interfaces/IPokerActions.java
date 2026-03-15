package com.ucm.server.interfaces;

public interface IPokerActions {
    
    public boolean call(final int amount);
    public boolean check();
    public boolean fold();
    public boolean raise(final int amount);
    public boolean allIn();

}