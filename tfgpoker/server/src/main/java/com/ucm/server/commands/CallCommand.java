package com.ucm.server.commands;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;



/**
 * Class that represents the Call command, which is used when a player wants to call the current bet.
 */
public class CallCommand extends Command {

    private static final Logger log = LogManager.getLogger(CallCommand.class);


    public CallCommand() {}

    /**
     * Constructor method that creates a CallCommand.
     * @param p the player that is making the call play.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public CallCommand(IPokerActions p, int money, int pocketMoney) {
        super(p, money, pocketMoney);
    }

    /* 
        If there is some bet on the table(maxBet is greater than zero) and the player has money(totalMoney is greater than zero)
        Even if the total money of the player is less than the maxBet, the player can call even if it is a smaller value.
    @Override
    public boolean validate(final int onBet, final int totalMoney, final int maxBet){
        return maxBet > 0 && totalMoney > 0;
    }
    */

    @Override
    public void requestParameters() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestParameters'");
    }

    @Override
    public boolean validate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        if(_currentHandBet == _playersOffBetMoney + _playersOnBetMoney){
            log.debug("Transform call command to all-in command");
            AllInCommand allIn = new AllInCommand(_player, _playersOffBetMoney, _playersOnBetMoney);
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

    @Override
    protected int getCommandNetworkCode() {
        return GameType.CALL_ACTION;
    }

}