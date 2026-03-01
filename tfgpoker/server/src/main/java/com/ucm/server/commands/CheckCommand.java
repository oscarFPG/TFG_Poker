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

    @Override
    public String getCommandName() {
        return "CHECK";
    }

    @Override
    public boolean matchCommand(String command) {
        return  command.equalsIgnoreCase("check") || 
                command.equalsIgnoreCase("k");
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
