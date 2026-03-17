package com.ucm.server.commands;


import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the Check command in the game.
 */
public class CheckCommand extends Command {


    public CheckCommand() {}

    /**
     * Constructor method that creates a CheckCommand.
     * @param p the player that is making the check play.
     */
    public CheckCommand(IPokerActions p) {
        super(p);
    }


    @Override
	protected Command createCommand(final String[] commandFormat, final IPokerActions player){
		return new CheckCommand(player);
	}

    @Override
    public boolean validate(final int maxBet) {
        return _playersOnBetMoney == 0 && maxBet == 0;
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        _player.check();
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
        return "check";
    }

    @Override
    public String getCommandFormatShortcut() {
        return "k";
    }

}
