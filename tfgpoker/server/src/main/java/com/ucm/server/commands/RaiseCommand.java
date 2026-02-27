package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


public class RaiseCommand extends Command {

    protected int _targetBet;

    public RaiseCommand(){}

    public RaiseCommand(Player p, int newBet, int money, int pocketMoney) {
        super(p, money, pocketMoney);
        _targetBet = newBet;
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        if (_targetBet == _money + _pocketMoney) {
            AllInCommand command = new AllInCommand(_player, _money + _pocketMoney);
            return command.execute(sb, bb, maxBet);
        }

        _player.raise(_targetBet);
        return CommandResult.continuePlaying(_targetBet, true);
    }

    @Override
    public boolean checkCommand() {
        return _money + _pocketMoney <= _targetBet;
    }

    @Override
    public String getCommandName() {
        return "RAISE";
    }

    @Override
    protected int getCommandIdentifier() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCommandIdentifier'");
    }

    @Override
    protected boolean correctCode(int codePlay) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'correctCode'");
    }

    @Override
    public Command create(int codePlay, Player p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }
}
