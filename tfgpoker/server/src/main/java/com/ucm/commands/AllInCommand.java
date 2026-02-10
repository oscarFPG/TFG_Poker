package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class AllInCommand extends Command {

    public AllInCommand(Player p, int money) {
        super(p, money, 0);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        _player.allIn();
        return CommandResult.continuePlaying(maxBet, false);
    }

    @Override
    public boolean checkCommand() {
       return true;
    }

    @Override
    public String getCommandName() {
        return "ALL-IN";
    }

}
