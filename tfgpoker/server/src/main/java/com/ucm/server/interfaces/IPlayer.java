package com.ucm.server.interfaces;

import com.ucm.server.commands.Command;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;


public interface IPlayer {

    public void onSendRole(PlayerRole r);
    public void onSendCard(Card c);
    public void onSendTableCard(Card c);
    public void onSendForcedMove(PlayerRole myRole, int sb, int bb);
    public Command onSendTurnPlay(final int maxBet);
    public void onSendRoundEnded();
    public void onSendHandEnded();
    public void onSendTurnWait();

}