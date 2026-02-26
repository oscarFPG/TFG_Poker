package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

/**
 * Class that represents the Raise command, which is a type of Command that a player can execute during a hand of poker.
 * When a player raises, they increase the current bet to a new amount, which must be higher than the current bet. 
 * The player must have enough money to cover the new bet amount.
 */
public class RaiseCommand extends Command {

    protected int _targetBet;
    /**
     * Constructor for the RaiseCommand class.
     * @param p the player who is raising
     * @param newBet the new bet amount that the player wants to raise to
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public RaiseCommand(Player p, int newBet, int money, int pocketMoney) {
        super(p, money, pocketMoney);
        _targetBet = newBet;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        if (_targetBet == _money + _pocketMoney) {
            AllInCommand command = new AllInCommand(_player, _money,_pocketMoney);
            return command.execute(sb, bb, maxBet);
        }

        _player.raise(_targetBet);
        return CommandResult.continuePlaying(_targetBet, true);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkCommand() {
        return _money + _pocketMoney >= _targetBet;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "RAISE";
    }
}
