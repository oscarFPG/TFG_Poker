package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

/**
 * Class that represents the All-in command, which is a type of Command that a player can execute during a hand of poker.
 * When a player goes All-in, they bet all of their remaining money on the current hand.
 */
public class AllInCommand extends Command {

    /**
     * Constructor method that creates an AllInCommand.
     * @param p the player that is making the All-in play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public AllInCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(!checkCommand())
            return null;

        _player.allIn();
        return CommandResult.continuePlaying(maxBet, false);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkCommand() {
       return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "ALL-IN";
    }

}
