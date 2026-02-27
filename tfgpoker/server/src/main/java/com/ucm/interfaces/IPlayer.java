package com.ucm.interfaces;

import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.PlayerRole;

public interface IPlayer {

    public void onReceiveRole(PlayerRole r);
    public void onReceiveCard(Card c);
    public void onReceiveTableCard(Card c);
    public void onReceiveTurn();

}
