package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class RaiseCommand extends Command {

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
}
