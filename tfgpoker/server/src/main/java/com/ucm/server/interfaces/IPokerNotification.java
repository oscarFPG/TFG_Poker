package com.ucm.server.interfaces;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;

public interface IPokerNotification {

    public void notifyPlayerRole(final PlayerRole role);
    public void notifyPlayerCard(final Card c);
    public void notifyTableCard(final Card c);
    public void notifySmallBlindBet(final int amount);
    public void notifyBigBlindBet(final int amount);
    public void notifyTurnWait();
    public void notifyTurnPlay();

    public void notifyRoundEnded();
    public void notifyHandEnded();
    public void notifyHandEndsByFolds();
    public void notifyGameEnded();
    public void notifyGameKeeps();

    public void notifyHandWinner();
    public void notifyHandLoser();
    public void notifyMoneyAmount(final int amount);

    public void notifyGameWinner();
    public void notifyGameLoser();
    public void notifyEquity(double equity);

    
}