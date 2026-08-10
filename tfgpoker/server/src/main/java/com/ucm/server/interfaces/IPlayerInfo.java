package com.ucm.server.interfaces;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;

/**
 * Interface to ONLY access player state(e.g: Name, On bet money, Off bet money, Player role, etc...)
 * 
 */
public interface IPlayerInfo {

    /**
     * Returns the unique identifier of the player.
     * @return the player's unique identifier
     */
    public int getPlayerId();

    /**
     * Returns the name of the player.
     * @return the player's name
     */
    public String getPlayerName();

    /**
     * Returns the amount of money the player has currently on bet.
     * @return the player's on-bet money
     */
    public int getMoneyOnBet();

    /**
     * Returns the amount of money the player has currently off bet.
     * @return the player's off-bet money
     */
    public int getMoneyOffBet();

    /**
     * Returns the cards held by the player.
     * @return an array of {@link Card} objects representing the player's cards
     */
    public Card[] getPlayerCards();

    /**
     * Returns the number of cards held by the player.
     * @return the count of cards the player has
     */
    public int getCardsCounter();

    /**
     * Returns whether the player has folded in the current hand.
     * @return true if the player has folded, false otherwise
     */
    public boolean isFolded();

    /**
     * Returns whether the player is the winner of the current hand.
     * @return true if the player is the winner, false otherwise
     */
    public boolean isWinner();

    /**
     * Returns whether the player has gone all-in.
     * @return true if the player is all-in, false otherwise
     */
    public boolean isAllIn();

    /**
     * Returns whether the player has been eliminated from the game.
     * @return true if the player is eliminated, false otherwise
     */
    public boolean isEliminated();

    /**
     * Returns the role of the player.
     * @return the player's role
     */
    public PlayerRole getRole();

    /**
     * Returns the last command executed by the player.
     * @return the last command as a string
     */
    public String getLastCommand();

    /**
     * Returns the name of the last rank got by the player. Ex.: "Pair", "Two Pair", "Three of a Kind", etc...
     * @return the name of the last rank
     */
    public String getLastRankName();

}