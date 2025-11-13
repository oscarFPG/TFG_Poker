package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

public class RaiseCommand extends Command {


    public RaiseCommand(Player p, int money) {
        super(p, money);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        _player.raise(_money);
        return CommandResult.continuePlaying(_money, true);     // _player.getPocketMoney() == this._pocketMoney
    }
}
