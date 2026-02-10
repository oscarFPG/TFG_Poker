package com.ucm.commands;

import java.io.IOException;

import com.ucm.GameType;
import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class CallCommand extends Command {

    public CallCommand(){
        super();
    };

    public CallCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        if(_currentBet == _money + _pocketMoney){
            AllInCommand allIn = new AllInCommand(_player, _money + _pocketMoney);
            return allIn.execute(sb, bb, maxBet);
        }
        
        _player.call(maxBet);
        return CommandResult.continuePlaying(_money, false);
    }

    @Override
    public boolean checkCommand() {
        return _currentBet <= _money + _pocketMoney;
    }

    @Override
    public String getCommandName() {
        return "CALL";
    }

    @Override
    protected int getCommandIdentifier(){
        return GameType.CALL;
    }

    @Override
    public Command create(int codePlay, Player p) throws IOException {
        //TODO controlar exception
        if(!correctCode(codePlay)) throw null;
        return new CallCommand(p, p.getMoney(), p.getPocketMoney());
    }

    @Override
    protected boolean correctCode(int codePlay) {
         return codePlay == GameType.CALL;
    }

    
}
