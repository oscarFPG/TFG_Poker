package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

/**
 * Class that represents the Check command in the game.
 */
public class CheckCommand extends Command {
    /**
     * Constructor method that creates a CheckCommand.
     * @param p the player that is making the check play.
     */
    public CheckCommand(Player p) {
        super(p, 0, 0);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        return CommandResult.continuePlaying(_money, false);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkCommand() {
        return _currentBet == 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "CHECK";
    }
}
