package com.ucm.server.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the fold command in the poker game. This command allows a player to fold and stop playing in the current hand.
 */
public class FoldCommand extends Command {

    private static final Logger log = LogManager.getLogger(FoldCommand.class);

    /**
     * Constructor to create an empty model of the Fold command
     */
    public FoldCommand() {}

    /**
     * Constructor method that creates a FoldCommand.
     * @param p the player that is making the fold play.
     */
    public FoldCommand(IPlayerActions p) {
        super(p);
    }


    /**
     * {@inheritDoc}
     */
    @Override
	protected Command createCommand(final String[] commandFormat, final IPlayerActions player){
        return new FoldCommand(player);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        log.debug("Player {} makes FOLD", _player.getPlayerName());

        _player.fold();

        if(PokerHistory.current() != null)
            PokerHistory.current().fold(_player);
        
        return CommandResult.stopPlaying();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "FOLD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "Retire from the current hand. If you have already bet, you will lose the money.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandFormat() {
        return GameType.FOLD_ACTION_FULL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandFormatShortcut() {
        return GameType.FOLD_ACTION_SHORTCUT;
    }

}
