package com.ucm.server.commands;

import java.io.InputStream;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the Check command in the game.
 */
public class CheckCommand extends Command {


    public CheckCommand(){
        super();
    }

    /**
     * Constructor method that creates a CheckCommand.
     * @param p the player that is making the check play.
     */
    public CheckCommand(Player p) {
        super(p, 0, 0);
    }

    /**
     * No other player has bet before(maxBet is zero), we have no bet before(onBet is zero) and we have some money(totalMoney is greater than zero).
     * The last check on the total money avoids executing this command if the player has been eliminated(It has no money left)
     */
    @Override
    public boolean validate(final int onBet, final int totalMoney, final int maxBet){
        return onBet == 0 && maxBet == 0 && totalMoney > 0;
    }

    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {
        return CommandResult.continuePlaying(_money, false);
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
    public String getCommandText() {
        return "check";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandTextShotcut() {
        return "k";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "If there is no bet just pass your turn without betting.";
    }

    @Override
    protected int getCommandNetworkCode() {
        return GameType.CHECK_ACTION;
    }

}
