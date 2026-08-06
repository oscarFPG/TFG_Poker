package com.ucm.server.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.interfaces.IPlayerActions;
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
    public CallCommand(IPlayerActions p) {
        super(p);
    }

	@Override
	protected Command createCommand(final String[] commandFormat, final IPlayerActions player){
		return new CallCommand(player);
	}

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(maxBet >= _playersOffBetMoney + _playersOnBetMoney){
            AllInCommand allIn = new AllInCommand(_player);
            return allIn.execute(sb, bb, maxBet);
        }
        else if(_playersOnBetMoney == 0 && maxBet == 0) {
            CheckCommand command = new CheckCommand(_player);
            return command.execute(sb, bb, maxBet);
        }
        
        
        _player.call(maxBet);

        if(PokerHistory.current() != null)
            PokerHistory.current().call(_player);
        
        log.debug("Player {} makes CALL", _player.getPlayerName());
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
        return GameType.CALL_ACTION_FULL;
    }

    @Override
    public String getCommandFormatShortcut() {
        return GameType.CALL_ACTION_SHORTCUT;
    }

}