package com.ucm.server.interfaces;

import java.io.IOException;

import com.ucm.common.BotStyle;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.exceptions.TurnTimeoutException;


/**
 * Core interface for notifying players about game events and requesting their actions during a poker game.
 * This interface defines methods for notifying players about various game events, such as their turn to play, actions taken by other players, and the state of the game.
 * It also includes methods for requesting player actions and providing feedback on their decisions.
 * Implementations of this interface are responsible for handling the communication with players, whether they are human players or AI bots.
 */
public interface IPlayerNotificator {

    /**
     * Notifies the player about their unique identifier (ID) in the game
     * @param sb small blind amount
     * @param bb big blind amount
     * @param maxBet maximum bet amount
     * @param minRaise minimum raise amount
     * @param player the player associated with this notificator
     * @return the action chosen by the player as a String
     * @throws IOException if there is an error during communication with the player
     * @throws TurnTimeoutException if the player takes too long to respond
     */
    public String notifyMakePlay(final int sb, final int bb, final int maxBet, final int minRaise, IPlayerInfo player) throws IOException, TurnTimeoutException;

    /**
     * Notifies the player the small blind bet made automatically made by him
     * @param amount small blind amount 
     * @param player the player associated with this notificator
     * @throws IOException if there is an error during communication with the player
     */
    public void notifySmallBlindBet(final int amount, IPlayerInfo player) throws IOException;

    /**
     * Notifies the player the big blind bet made automatically made by him
     * @param amount big blind amount 
     * @param player the player associated with this notificator
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyBigBlindBet(final int amount, IPlayerInfo player) throws IOException;

    /**
     * Notifies the player about their assigned role in the game (e.g., dealer, small blind, big blind)
     * @param role the role assigned to the player
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyPlayerRole(final PlayerRole role) throws IOException;

    /**
     * Notifies the player about a card received in their hand or on the table
     * @param c the card to be notified
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyPlayerCard(final Card c) throws IOException;

    /**
     * Notifies the player about a card revealed on the table (community card)
     * @param c the card to be notified
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyTableCard(final Card c) throws IOException;

    /**
     * Notifies the player the total pot amount
     * @param total the total pot amount
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyTotalPot(final int total) throws IOException;

    /**
     * Notifies the player about an action performed by another player in the game
     * @param other the other player performing the action
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException;

    /**
     * Notifies the player about their state after performing an action (e.g., after betting, calling, folding, etc.)
     * @param player the player whose state is being notified
     * @param receiveRank indicates if the player should receive their rank information
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyOwnState(IPlayerInfo player, final boolean receiveRank) throws IOException;

    /**
     * Notifies the player about their current state in the game. 
     * This method is triggered multiple times at the end of each hand
     * @param other the other player whose state is being notified 
     * @param receiveRank indicates if the player should receive their rank information
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyOtherPlayerState(IPlayerInfo other, final boolean receiveRank) throws IOException;

    /**
     * Notifies when server stops sending more player states info
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyEndPlayerState() throws IOException;

    /**
     * Notifies the player before an action has been performed by another player in the game
     * @param player the player performing the action
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyCurrentTurnPlayer(IPlayerInfo player) throws IOException;

    /**
     * Notifies the player that it's other player's turn to play
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyTurnWait() throws IOException;

    /**
     * Notifies the player that it's their turn to play and they should decide an action
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyTurnPlay() throws IOException;

    /**
     * Notifies the player that a round has ended (pre-flop + flop + turn + river + showdown)
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyRoundEnded() throws IOException;

    /**
     * Notifies the player that a hand has ended due to all but one player folding
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyHandEndsByFolds() throws IOException;

    /**
     * Notifies the player that the game has ended
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyGameEnded() throws IOException;

    /**
     * Notifies the player that the game is ongoing and they should wait for the next hand to start
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyGameKeeps() throws IOException;

    /**
     * Notifies the player that they have won the game
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyGameWinner() throws IOException;

    /**
     * Notifies the player that they have lost the game
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyGameLoser() throws IOException;

    /**
     * Notifies the player about their current equity in the hand (chance of winning based on their cards and the table)
     * @param equity the player's current equity
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyEquity(double equity) throws IOException;
    
    /**
     * Notifies the cards from an specific player. The other player and this player should not be the same
     * @param other the other player whose cards are being notified
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyOtherPlayerCards(IPlayerInfo other) throws IOException;

    /**
     * Notifies the player about their unique identifier (ID) in the game
     * @param player the player whose ID is being notified
     * @throws IOException if there is an error during communication with the player
     */
    public void notifyPlayerID(IPlayerInfo player) throws IOException;

    /**
     * Returns the style of the bot associated with this notificator, if applicable. This method is relevant for AI players and can be used to determine their playing style or behavior.
     * @return the bot style associated with this notificator, or null if not applicable
     * @see BotStyle
     */
    public BotStyle getStyle();

    /**
     * Returns the type of player associated with this notificator (e.g., human, AI bot). This method can be used to differentiate between different types of players in the game.
     * @return the type of player associated with this notificator
     */
    public String getPlayerType();

    /**
     * Returns the model or representation of the player associated with this notificator. This method can be used to retrieve information about the player's characteristics, preferences, or other relevant details.
     * @return the model or representation of the player associated with this notificator
     */
    public String getPlayerModel();
}