package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


public class AllInCommand extends Command {

    public AllInCommand(){}

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

    @Override
    protected int getCommandIdentifier() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCommandIdentifier'");
    }

    @Override
    protected boolean correctCode(int codePlay) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'correctCode'");
    }

    @Override
    public Command create(int codePlay, Player p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

}
