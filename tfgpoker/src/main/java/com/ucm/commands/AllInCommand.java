package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

public class AllInCommand extends Command {

    public AllInCommand(Player p, int value) {
        super(p, value);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        this._player.allIn();
        return CommandResult.stopPlaying(this._player.getPocketMoney());
    }
}
