package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the fold command in the poker game. This command allows a player to fold and stop playing in the current hand.
 */
public class FoldCommand extends Command {

    public FoldCommand(){
        super();
    }

    /**
     * Constructor method that creates a FoldCommand.
     * @param p the player that is making the fold play.
     */
    public FoldCommand(Player p) {
        super(p, 0, 0);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        /*
         * Player always can fold, no need to call the checkCommand() method
         */
        _player.fold();
        return CommandResult.stopPlaying(0);
    }

    @Override
    public String getCommandName() {
        return "FOLD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public boolean matchCommand(String command) {
        return  command.equalsIgnoreCase("fold") || 
                command.equalsIgnoreCase("f");
    }

    @Override
    public boolean checkAttributes(String[] fullCommand) {
        return fullCommand.length == 1;
    }

    @Override
    public Command create(String[] fullCommand, Player p) {
        return new FoldCommand(p);
    }

}
