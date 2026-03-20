package com.ucm.server.commands;


import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;

/**
 * Class that represents the All-in command, which is a type of Command that a player can execute during a hand of poker.
 * When a player goes All-in, they bet all of their remaining money on the current hand.
 */
public class AllInCommand extends Command {


    public AllInCommand() {}

    /**
     * Constructor method that creates an AllInCommand.
     * @param p the player that is making the All-in play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public AllInCommand(IPokerActions p) {
        super(p);
    }


    @Override
	protected Command createCommand(final String[] commandFormat, final IPokerActions player){
		return new AllInCommand(player);
	}

    @Override
    public boolean validate(final int maxBet) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        _player.allIn();
        return CommandResult.continuePlaying(_player.getMoneyOnBet(), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "ALL-IN";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "Bet all of your remaining money on the current hand.";
    }


    @Override
    public String getCommandFormat() {
        return "allin";
    }

    @Override
    public String getCommandFormatShortcut() {
        return "a";
    }

}