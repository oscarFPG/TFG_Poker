package com.ucm.server.middleclasses;

/**
 * Represents the result of a player's action during a betting round.
 *
 * @param bet    the total amount of money committed by the player
 * @param raises indicates whether the action was a raise
 * @param folds  indicates whether the player folded
 */
public record CommandResult(int bet, boolean raises, boolean folds) {

    /**
     * Creates a result for an action where the player continues playing.
     *
     * @param money the total bet after the action
     * @param raise true if the player raised the bet
     * @return a CommandResult representing that the player continues playing
     */
    public static CommandResult continuePlaying(int money, boolean raise) {
        return new CommandResult(money, raise, false);
    }

    /**
     * Creates a result for an action where the player folds.
     *
     * @param money the total bet committed before folding
     * @return a CommandResult representing a folding action
     */
    public static CommandResult stopPlaying() {
        return new CommandResult(0, false, true);
    }
}