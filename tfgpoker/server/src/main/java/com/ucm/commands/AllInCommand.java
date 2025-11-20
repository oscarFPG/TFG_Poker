package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public class AllInCommand extends Command {

    public AllInCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        _player.allIn();
        return CommandResult.stopPlaying(_money); // _player.getPocketMoney() == this._money
    }

    @Override
    public boolean checkCommand() {
       return false;
    }

    @Override
    public String getCommandName() {
        return "ALL-IN";
    }

}
