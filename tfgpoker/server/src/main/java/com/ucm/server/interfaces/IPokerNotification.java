package com.ucm.server.interfaces;

import java.io.IOException;
import java.util.List;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;



public interface IPokerNotification {

    /**
     * Determines the action to take during the player's turn.
     * 
     * @param sb     small blind amount
     * @param bb     big blind amount
     * @param maxBet current maximum bet
     * @return {@link String} representing the chosen action
     */
    public String notifyMakePlay(final int sb, final int bb, final int maxBet) throws IOException;

    /**
     * Notifies the player the small blind bet made automatically made by him
     * 
     *  @param amount small blind amount 
     */
    public void notifySmallBlindBet(final int amount) throws IOException;

    /**
     * Notifies the player the big blind bet made automatically made by him
     * 
     *  @param amount big blind amount 
     */
    public void notifyBigBlindBet(final int amount) throws IOException;

    /**
     * Notifies the player about their assigned role in the game (e.g., dealer, small blind, big blind)
     * @param role
     * @throws IOException
     */
    public void notifyPlayerRole(final PlayerRole role) throws IOException;
    public void notifyPlayerCard(final Card c) throws IOException;
    public void notifyTableCard(final Card c) throws IOException;


    /**
     * Notifies the player the total pot amount
     * @param total
     * @throws IOException
     */
    public void notifyTotalPot(final int total) throws IOException;
    public void notifyOtherPlayerAction(IPokerPlayer p) throws IOException;
    public void notifyTurnWait() throws IOException;
    public void notifyTurnPlay() throws IOException;
    public void notifyPlayerState(final IPokerPlayer player, final boolean last) throws IOException;


    /**
     * Notifies the player that a round has ended (pre-flop + flop + turn + river + showdown)
     * @throws IOException
     */
    public void notifyRoundEnded() throws IOException;
    public void notifyHandEnded() throws IOException;
    public void notifyHandEndsByFolds() throws IOException;
    public void notifyGameEnded() throws IOException;
    public void notifyGameKeeps() throws IOException;
    public void notifyMoneyAmount(final int amount) throws IOException;

    public void notifyGameWinner() throws IOException;
    public void notifyGameLoser() throws IOException;

    public void notifyOwnState() throws IOException;

    /**
     * Notifies the player about their current state in the game. 
     * This method is triggered multiple times at the end of each hand
     * @param player
     * @param isLast indicates if this is the last player sent
     * @throws IOException
     */
    public void notifyOtherPlayerState(IPokerPlayer player, boolean isLast) throws IOException;

    /**
     * Notifies the player about their current equity in the hand (chance of winning based on their cards and the table)
     * @param equity
     * @throws IOException
     */
    public void notifyEquity(double equity) throws IOException;
}
