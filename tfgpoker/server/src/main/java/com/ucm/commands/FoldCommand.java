package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

public class FoldCommand extends Command {

    public FoldCommand(Player p, int money) {
        super(p, money);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        _player.fold();
        return CommandResult.stopPlaying(_money);   // _player.getPocketMoney() == this._pocketMoney
    }

}
