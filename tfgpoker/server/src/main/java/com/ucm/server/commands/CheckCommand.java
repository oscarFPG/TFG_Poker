package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the Check command in the game.
 */
public class CheckCommand extends Command {


    public CheckCommand(){
        super();
    }

    /**
     * Constructor method that creates a CheckCommand.
     * @param p the player that is making the check play.
     */
    public CheckCommand(Player p) {
        super(p, 0, 0);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        return CommandResult.continuePlaying(_money, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "CHECK";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandText() {
        return "check";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandTextShotcut() {
        return "k";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "If there is no bet just pass your turn without betting.";
    }

    @Override
    public boolean checkAttributes(String[] fullCommand) {
        return fullCommand.length == 1;
    }

    @Override
    public Command create(String[] fullCommand, Player p) {
        return new CheckCommand(p);
    }



}
