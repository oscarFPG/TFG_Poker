package com.ucm.server.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the Check command in the game.
 */
public class CheckCommand extends Command {

    private static final Logger log = LogManager.getLogger(CheckCommand.class);


    public CheckCommand() {}

    /**
     * Constructor method that creates a CheckCommand.
     * @param p the player that is making the check play.
     */
    public CheckCommand(IPlayerActions p) {
        super(p);
    }


    @Override
	protected Command createCommand(final String[] commandFormat, final IPlayerActions player){
		return new CheckCommand(player);
	}

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(maxBet != 0) {
            CallCommand command = new CallCommand(_player);
            return command.execute(sb, bb, maxBet);
        }

        
        _player.check();

        if(PokerHistory.current() != null)
            PokerHistory.current().check(_player);
        
        log.debug("Player {} makes CHECK", _player.getPlayerName());
        return CommandResult.continuePlaying(0, false);
    }

    @Override
    public String getCommandName() {
        return "CHECK";
    }

    @Override
    public String getCommandDescription() {
        return "If there is no bet just pass your turn without betting.";
    }

    @Override
    public String getCommandFormat() {
        return GameType.CHECK_ACTION_FULL;
    }

    @Override
    public String getCommandFormatShortcut() {
        return GameType.CHECK_ACTION_SHORTCUT;
    }

}
