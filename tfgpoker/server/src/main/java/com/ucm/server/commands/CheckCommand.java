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

    /**
     * Default constructor for the CheckCommand class.
     */
    public CheckCommand() {}

    /**
     * Constructor method that creates a CheckCommand.
     * @param p the player that is making the check play.
     */
    public CheckCommand(IPlayerActions p) {
        super(p);
    }


    /**
     * {@inheritDoc}
     */
    @Override
	protected Command createCommand(final String[] commandFormat, final IPlayerActions player){
		return new CheckCommand(player);
	}

    /**
     * {@inheritDoc}
     * 
     * This method can create a {@link CallCommand} and execute it under the right conditions, and execute it instead of the CheckCommand.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "CHECK";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "If there is no bet just pass your turn without betting.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandFormat() {
        return GameType.CHECK_ACTION_FULL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandFormatShortcut() {
        return GameType.CHECK_ACTION_SHORTCUT;
    }

}
