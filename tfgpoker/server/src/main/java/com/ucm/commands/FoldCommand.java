package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class FoldCommand extends Command {

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
    public boolean checkCommand() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "FOLD";
    }

}
