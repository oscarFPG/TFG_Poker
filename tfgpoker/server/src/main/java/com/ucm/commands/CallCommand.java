package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;

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
