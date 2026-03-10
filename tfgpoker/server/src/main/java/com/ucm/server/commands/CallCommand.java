package com.ucm.server.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;



/**
 * Class that represents the Call command, which is used when a player wants to call the current bet.
 */
public class CallCommand extends Command {

    private static final Logger log = LogManager.getLogger(CallCommand.class);

    public CallCommand(){
        super();
    }

    /**
     * Constructor method that creates a CallCommand.
     * @param p the player that is making the call play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public CallCommand(Player p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    /**
     * If there is some bet on the table(maxBet is greater than zero) and the player has money(totalMoney is greater than zero)
     * Even if the total money of the player is less than the maxBet, the player can call even if it is a smaller value.
     */
    @Override
    public boolean validate(final int onBet, final int totalMoney, final int maxBet){
        return maxBet > 0 && totalMoney > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(_currentBet == _money + _pocketMoney){
            log.debug("Transform call command to all-in command");
            AllInCommand allIn = new AllInCommand(_player, _money, _pocketMoney);
            return allIn.execute(sb, bb, maxBet);
        }
        
        log.debug("Executing the call command with value to bet {}", maxBet);
        _player.call(maxBet);
        return CommandResult.continuePlaying(maxBet, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "CALL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandText() {
        return "call";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandTextShotcut() {
        return "c";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "Call the current bet.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkAttributes(String[] fullCommand) {
        return fullCommand.length == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command create(String[] fullCommand, Player p) {
        return new CallCommand(p, p.getMoney(), p.getPocketMoney());
    }

    @Override
    protected int getCommandNetworkCode() {
        return GameType.CALL_ACTION;
    }
}