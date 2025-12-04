package com.ucm.commands;

import java.io.IOException;

import com.ucm.GameType;
import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class FoldCommand extends Command {

    public FoldCommand(){
        super();
    }

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

    @Override
    protected int getCommandIdentifier(){
        return GameType.FOLD;
    }
    
    @Override
    public Command create(int codePlay, Player p) throws IOException {
        //TODO controlar exception
        if(!correctCode(codePlay)) throw null;
        return new FoldCommand(p);
    }

    @Override
    protected boolean correctCode(int codePlay) {
        return codePlay == GameType.FOLD;
    }
}
