package com.ucm.server.interfaces;


/**
 * Interface representing the actions that a player can perform in a poker game.
 * This interface extends the {@link IPlayerInfo} interface, which provides information about the player.
 * This is mostly used by the commands
 * @see IPlayerInfo
 * @see Command
 * @see AllInCommand
 * @see CallCommand
 * @see CheckCommand
 * @see FoldCommand
 * @see RaiseCommand
 */
public interface IPlayerActions extends IPlayerInfo {
    
    /**
     * Performs a call action for the player, matching the current bet amount.
     * @param amount the amount to call, which is the difference between the current bet and the player's on-bet money
     */
    public void call(final int amount);

    /**
     * Performs a check action for the player, indicating that they do not want to bet any additional money in the current round.
     */
    public void check();

    /**
     * Performs a fold action for the player, indicating that they are withdrawing from the current hand and forfeiting any bets they have made.
     */
    public void fold();

    /**
     * Performs a raise action for the player, increasing the current bet amount by the specified amount.
     * @param amount the amount to raise, which is added to the player's on-bet money and deducted from their off-bet money
     */
    public void raise(final int amount);

    /**
     * Performs an all-in action for the player, betting all of their remaining off-bet money.
     */
    public void allIn();

}