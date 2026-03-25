package com.ucm.server.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;

/**
 * Class that represents the Raise command, which is a type of Command that a player can execute during a hand of poker.
 * When a player raises, they increase the current bet to a new amount, which must be higher than the current bet. 
 * The player must have enough money to cover the new bet amount.
 */
public class RaiseCommand extends Command {

    private static final Logger log = LogManager.getLogger(RaiseCommand.class);

    protected int _targetBet;
    

    public RaiseCommand() {}

    /**
     * Constructor for the RaiseCommand class.
     * @param p the player who is raising
     * @param newBet the new bet amount that the player wants to raise to
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public RaiseCommand(IPokerActions p, int targetBet) {
        super(p);
        _targetBet = targetBet;
    }
    

    @Override
    protected Command createCommand(String[] commandFormat, IPokerActions player) {
        
        if(commandFormat.length != 2){
            log.error("The RaiseCommand must have an argument <amount>");
            return null;
        } 

        try {
            int target = Integer.parseInt( commandFormat[1] );
            return new RaiseCommand(player, target);
        }
        catch (NumberFormatException e) {
            log.error("Trying to parse as an integer the string {}", commandFormat[1]);
            return null;
        }
    }

    @Override
    public boolean validate(final int maxBet) {
        return 0 <= _targetBet &&  maxBet < _targetBet && _targetBet <= _playersOnBetMoney + _playersOffBetMoney;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if (_targetBet == _playersOffBetMoney + _playersOnBetMoney) {
            AllInCommand command = new AllInCommand(_player);
            return command.execute(sb, bb, maxBet);
        }

        _player.raise(_targetBet);
        return CommandResult.continuePlaying(_targetBet, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "RAISE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "Increase the current bet to a new amount.";
    }

    @Override
    public String getCommandFormat() {
        return "raise";
    }

    @Override
    public String getCommandFormatShortcut() {
        return "r";
    }

}
