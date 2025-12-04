package com.ucm.commands;

import java.io.IOException;

import com.ucm.GameType;
import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class CheckCommand extends Command {

    public CheckCommand(){
        super();
    }

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

    @Override
    protected int getCommandIdentifier(){
        return GameType.CHECK;
    }

    @Override
    public Command create(int codePlay, Player p) throws IOException {
        //TODO controlar exception
        if(!correctCode(codePlay)) throw null;
        return new CheckCommand(p);
    }

    @Override
    protected boolean correctCode(int codePlay) {
       return codePlay == GameType.CHECK;
    }
}
