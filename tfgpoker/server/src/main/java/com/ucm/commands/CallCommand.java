package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

/**
 * Class that represents the Call command, which is used when a player wants to call the current bet.
 */
public class CallCommand extends Command {
    /**
     * Constructor method that creates a CallCommand.
     * @param p the player that is making the call play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public CallCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        if(_currentBet == _money + _pocketMoney){
            AllInCommand allIn = new AllInCommand(_player, _money, _pocketMoney);
            return allIn.execute(sb, bb, maxBet);
        }
        
        _player.call(maxBet);
        return CommandResult.continuePlaying(_money, false);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkCommand() {
        return _currentBet <= _money + _pocketMoney;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "CALL";
    }
}
