package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

public class CallCommand extends Command {

    public CallCommand(Player p, int money) {
        super(p, money);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {  
        _player.call(maxBet);
        return CommandResult.continuePlaying(_money, false);  // _player.getPocketMoney() == this._pocketMoney
    }
}
