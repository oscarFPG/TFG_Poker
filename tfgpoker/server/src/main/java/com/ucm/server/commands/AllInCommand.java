package com.ucm.server.commands;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.middleclasses.CommandResult;

/**
 * Class that represents the All-in command, which is a type of Command that a player can execute during a hand of poker.
 * When a player goes All-in, they bet all of their remaining money on the current hand.
 */
public class AllInCommand extends Command {

    private static final Logger log = LogManager.getLogger(AllInCommand.class);

    
    public AllInCommand() {}

    /**
     * Constructor method that creates an AllInCommand.
     * @param p the player that is making the All-in play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public AllInCommand(IPlayerActions p) {
        super(p);
    }


    @Override
	protected Command createCommand(final String[] commandFormat, final IPlayerActions player){
		return new AllInCommand(player);
	}

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        log.debug("Player {} makes ALL-IN", _player.getPlayerName());

        _player.allIn();

        if(PokerHistory.current() != null)
            PokerHistory.current().allIn(_player);
        
        int playerBet = _player.getMoneyOnBet();
        return CommandResult.continuePlaying(playerBet, playerBet > maxBet);
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
        return GameType.ALL_IN_ACTION_FULL;
    }

    @Override
    public String getCommandFormatShortcut() {
        return GameType.ALL_IN_ACTION_SHORTCUT;
    }

}