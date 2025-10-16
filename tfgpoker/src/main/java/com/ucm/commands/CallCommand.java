package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

public class CallCommand extends Command {

    public CallCommand(Player p, int value) {
        super(p, value);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        this._player.call(maxBet);

        return CommandResult.continuePlaying(this._player.getPocketMoney(), false);
    }
}
