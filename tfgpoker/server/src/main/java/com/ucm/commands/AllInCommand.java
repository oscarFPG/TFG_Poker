package com.ucm.commands;

import java.io.IOException;

import com.ucm.GameType;
import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class AllInCommand extends Command {

    public AllInCommand(){
        super();
    }

    public AllInCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        _player.allIn();
        return CommandResult.stopPlaying(_money); // _player.getPocketMoney() == this._money
    }

    @Override
    public boolean checkCommand() {
       return false;
    }

    @Override
    public String getCommandName() {
        return "ALL-IN";
    }

    @Override
    protected int getCommandIdentifier(){
        return GameType.ALL_IN;
    }

    @Override
    public Command create(int codePlay, Player p) throws IOException {
         //TODO controlar exception
        if(!correctCode(codePlay)) throw null;
        return new AllInCommand(p, p.getMoney(), p.getPocketMoney());
    }

    @Override
    protected boolean correctCode(int codePlay) {
        return codePlay == GameType.ALL_IN;
    }
}
