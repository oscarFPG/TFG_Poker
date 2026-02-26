package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

/**
 * Class that represents the fold command in the poker game. This command allows a player to fold and stop playing in the current hand.
 */
public class FoldCommand extends Command {
    /**
     * Constructor method that creates a FoldCommand.
     * @param p the player that is making the fold play.
     */
    public FoldCommand(Player p) {
        super(p, 0, 0);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        /*
         * Player always can fold, no need to call the checkCommand() method
         */
        _player.fold();
        return CommandResult.stopPlaying(0);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkCommand() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "FOLD";
    }

}
