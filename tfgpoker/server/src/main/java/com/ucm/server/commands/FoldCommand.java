package com.ucm.server.commands;


import com.ucm.common.GameType;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the fold command in the poker game. This command allows a player to fold and stop playing in the current hand.
 */
public class FoldCommand extends Command {


    public FoldCommand() {}

    /**
     * Constructor method that creates a FoldCommand.
     * @param p the player that is making the fold play.
     */
    public FoldCommand(IPlayerActions p) {
        super(p);
    }


    @Override
	protected Command createCommand(final String[] commandFormat, final IPlayerActions player){
        return new FoldCommand(player);
	}

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        _player.fold();
        return CommandResult.stopPlaying();
    }

    @Override
    public String getCommandName() {
        return "FOLD";
    }

    @Override
    public String getCommandDescription() {
        return "Retire from the current hand. If you have already bet, you will lose the money.";
    }

    @Override
    public String getCommandFormat() {
        return GameType.FOLD_ACTION_FULL;
    }

    @Override
    public String getCommandFormatShortcut() {
        return GameType.FOLD_ACTION_SHORTCUT;
    }

}
