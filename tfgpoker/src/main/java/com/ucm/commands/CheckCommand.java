package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

public class CheckCommand extends Command {

    public CheckCommand(Player p, int value) {
        super(p, value);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        return CommandResult.continuePlaying(this._player.getPocketMoney(), false);
    }
}
