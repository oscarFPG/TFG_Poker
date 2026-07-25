package com.ucm.server.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.interfaces.IPlayerActions;
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
    public RaiseCommand(IPlayerActions p, int targetBet) {
        super(p);
        _targetBet = targetBet;
    }
    

    @Override
    protected Command createCommand(String[] commandFormat, IPlayerActions player) {
        
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
    public CommandResult execute(int sb, int bb, int maxBet) {

        if (_targetBet >= _playersOffBetMoney + _playersOnBetMoney) {   // All-in -> Raise <total_money>
            AllInCommand command = new AllInCommand(_player);
            return command.execute(sb, bb, maxBet);
        }
        else if(_targetBet == 0 && _playersOnBetMoney == 0 && maxBet == 0) { // Check -> Raise 0
            CheckCommand command = new CheckCommand(_player);
            return command.execute(sb, bb, maxBet);
        }
        else if(0 < maxBet && _targetBet <= maxBet) { // Call -> Raise <maxBet>
            CallCommand command = new CallCommand(_player);
            return command.execute(sb, bb, maxBet);
        }
        
        
        // Normal raise
        log.debug("Player {} makes RAISE {}", _player.getPlayerName(), _targetBet);

        _player.raise(_targetBet);
        PokerHistory.current().raise(_player);
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
        return GameType.RAISE_ACTION_FULL;
    }

    @Override
    public String getCommandFormatShortcut() {
        return GameType.RAISE_ACTION_SHORTCUT;
    }

}
