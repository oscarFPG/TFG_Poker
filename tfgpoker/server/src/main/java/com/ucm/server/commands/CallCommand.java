package com.ucm.server.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;



/**
 * Class that represents the Call command, which is used when a player wants to call the current bet.
 */
public class CallCommand extends Command {

    private static final Logger log = LogManager.getLogger(CallCommand.class);


    public CallCommand() {}

    /**
     * Constructor method that creates a CallCommand.
     * @param p the player that is making the call play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public CallCommand(IPokerActions p) {
        super(p);
    }

	@Override
	protected Command createCommand(final String[] commandFormat, final IPokerActions player){
		return new CallCommand(player);
	}

	@Override
    public boolean validate(final int maxBet) {
		return maxBet > 0;
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(_currentHandBet == _playersOffBetMoney + _playersOnBetMoney){
            log.debug("Transform call command to all-in command");
            AllInCommand allIn = new AllInCommand(_player);
            return allIn.execute(sb, bb, maxBet);
        }
        
        log.debug("Executing the call command with value to bet {}", maxBet);
        _player.call(maxBet);
        return CommandResult.continuePlaying(maxBet, false);
    }

    @Override
    public String getCommandName() {
        return "CALL";
    }

    @Override
    public String getCommandDescription() {
        return "Call the current bet.";
    }

    @Override
    public String getCommandFormat() {
        return "call";
    }

    @Override
    public String getCommandFormatShortcut() {
        return "c";
    }

}