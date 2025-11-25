package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class CallCommand extends Command {

    public CallCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        if(_currentBet == _money + _pocketMoney){
            AllInCommand allIn = new AllInCommand(_player, _money, _pocketMoney);
            return allIn.execute(sb, bb, maxBet);
        }
        else{
            _player.call(maxBet);
            return CommandResult.continuePlaying(_money, false);
        }
    }

    @Override
    public boolean checkCommand() {
        return _currentBet <= _money + _pocketMoney;
    }

    @Override
    public String getCommandName() {
        return "CALL";
    }
}
