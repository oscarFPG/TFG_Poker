package com.ucm.server.interfaces;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

public interface IPlayer {

    public void onReceiveRole(PlayerRole r);
    public void onReceiveCard(Card c);
    public void onReceiveTableCard(Card c);
    public void onReceiveTurn();

}
