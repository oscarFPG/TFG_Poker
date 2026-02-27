package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


public class CheckCommand extends Command {

    public CheckCommand(Player p) {
        super(p, 0, 0);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        return CommandResult.continuePlaying(_money, false);
    }

    @Override
    public boolean checkCommand() {
        return _currentBet == 0;
    }

    @Override
    public String getCommandName() {
        return "CHECK";
    }
}
