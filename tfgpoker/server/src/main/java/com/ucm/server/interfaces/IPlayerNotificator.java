package com.ucm.server.interfaces;

import java.io.IOException;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;



public interface IPlayerNotificator {

    /**
     * Determines the action to take during the player's turn.
     * 
     * @param sb     small blind amount
     * @param bb     big blind amount
     * @param maxBet current maximum bet
     * @return {@link String} representing the chosen action
     */
    public String notifyMakePlay(final int sb, final int bb, final int maxBet, IPlayerInfo player) throws IOException;

    /**
     * Notifies the player the small blind bet made automatically made by him
     * 
     *  @param amount small blind amount 
     */
    public void notifySmallBlindBet(final int amount, IPlayerInfo player) throws IOException;

    /**
     * Notifies the player the big blind bet made automatically made by him
     * 
     *  @param amount big blind amount 
     */
    public void notifyBigBlindBet(final int amount, IPlayerInfo player) throws IOException;

    /**
     * Notifies the player about their assigned role in the game (e.g., dealer, small blind, big blind)
     * @param role
     * @throws IOException
     */
    public void notifyPlayerRole(final PlayerRole role) throws IOException;

    /**
     * Notifies the player about a card received in their hand or on the table
     * @param c
     * @throws IOException
     */
    public void notifyPlayerCard(final Card c) throws IOException;

    /**
     * Notifies the player about a card revealed on the table (community card)
     * @param c
     * @throws IOException
     */
    public void notifyTableCard(final Card c) throws IOException;


    /**
     * Notifies the player the total pot amount
     * @param total
     * @throws IOException
     */
    public void notifyTotalPot(final int total) throws IOException;

    /**
     * Notifies the player about an action performed by another player in the game
     * @param p the player performing the action
     * @throws IOException
     */
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException;

    /**
     * Notifies the player about their state after performing an action (e.g., after betting, calling, folding, etc.)
     * @throws IOException
     */
    public void notifyOwnState(IPlayerInfo player) throws IOException;

    /**
     * Notifies the player about their current state in the game. 
     * This method is triggered multiple times at the end of each hand
     * @param player
     * @param isLast indicates if this is the last player sent
     * @throws IOException
     */
    public void notifyOtherPlayerState(IPlayerInfo other, boolean isLast) throws IOException;

    /**
     * Notifies the player before an action has been performed by another player in the game
     * @param p the player performing the action
     * @throws IOException
     */
    public void notifyCurrentTournPlayer(IPokerPlayer p) throws IOException;

    /**
     * Notifies the player that it's other player's turn to play
     * @throws IOException
     */
    public void notifyTurnWait() throws IOException;

    /**
     * Notifies the player that it's their turn to play and they should decide an action
     * @throws IOException
     */
    public void notifyTurnPlay() throws IOException;

    /**
     * Notifies the player that a round has ended (pre-flop + flop + turn + river + showdown)
     * @throws IOException
     */
    public void notifyRoundEnded() throws IOException;

    /**
     * Notifies the player that a hand has ended
     * @throws IOException
     */
    public void notifyHandEnded() throws IOException;

    /**
     * Notifies the player that a hand has ended due to all but one player folding
     * @throws IOException
     */
    public void notifyHandEndsByFolds() throws IOException;

    /**
     * Notifies the player that the game has ended
     * @throws IOException
     */
    public void notifyGameEnded() throws IOException;

    /**
     * Notifies the player that the game is ongoing and they should wait for the next hand to start
     * @throws IOException
     */
    public void notifyGameKeeps() throws IOException;

    /**
     * Notifies the player that they have won the game
     * @throws IOException
     */
    public void notifyGameWinner() throws IOException;

    /**
     * Notifies the player that they have lost the game
     * @throws IOException
     */
    public void notifyGameLoser() throws IOException;

    /**
     * Notifies the player about their current equity in the hand (chance of winning based on their cards and the table)
     * @param equity
     * @throws IOException
     */
    public void notifyEquity(double equity) throws IOException;

}