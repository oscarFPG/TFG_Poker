package com.ucm.commands;

import java.io.IOException;

import com.ucm.GameType;
import com.ucm.gameobjects.Player;
import com.ucm.logic.Game;
import com.ucm.middleclasses.CommandResult;


public class RaiseCommand extends Command {

    public RaiseCommand(){
        super();
    }

    public RaiseCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        _player.raise(_money);
        return CommandResult.continuePlaying(_money, true);     // _player.getPocketMoney() == this._pocketMoney
    }

    @Override
    public boolean checkCommand() {
        return false;
    }

    @Override
    public String getCommandName() {
        return "RAISE";
    }

    @Override
    protected int getCommandIdentifier(){
        return GameType.RAISE;
    }

    @Override
    public Command create(int codePlay, Player p) throws IOException {
        //TODO controlar exception
        if(!correctCode(codePlay)) throw null;
        return new RaiseCommand(p, p.getMoney(), p.getPocketMoney());
    }

    @Override
    protected boolean correctCode(int codePlay) {
        return codePlay == GameType.RAISE;
    }



    

}
