package com.ucm.server.interfaces;


// Interface to allow Commands to interact with the player
public interface IPlayerActions extends IPlayerInfo {
    
    // Actions
    public void call(final int amount);
    public void check();
    public void fold();
    public void raise(final int amount);
    public void allIn();

}