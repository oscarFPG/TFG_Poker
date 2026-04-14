package com.ucm.server.interfaces;

import java.io.IOException;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;



public interface IPokerNotification {

    public void notifyPlayerRole(final PlayerRole role) throws IOException;
    public void notifyPlayerCard(final Card c) throws IOException;
    public void notifyTableCard(final Card c) throws IOException;
    public void notifySmallBlindBet(final int amount) throws IOException;
    public void notifyBigBlindBet(final int amount) throws IOException;
    public void notifyTotalPot(final int total) throws IOException;
    public void notifyOtherPlayerAction(IPokerPlayer p) throws IOException;
    public void notifyTurnWait() throws IOException;
    public void notifyTurnPlay() throws IOException;
    public void notifyPlayerState(final IPokerPlayer player, final boolean last) throws IOException;

    public void notifyRoundEnded() throws IOException;
    public void notifyHandEnded() throws IOException;
    public void notifyHandEndsByFolds() throws IOException;
    public void notifyGameEnded() throws IOException;
    public void notifyGameKeeps() throws IOException;
    public void notifyMoneyAmount(final int amount) throws IOException;

    public void notifyGameWinner() throws IOException;
    public void notifyGameLoser() throws IOException;
    public void notifyEquity(double equity);

    
}