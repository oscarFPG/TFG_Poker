package com.ucm.server.commands;

import java.io.IOException;
import java.io.InputStream;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;

/**
 * Class that represents the Raise command, which is a type of Command that a player can execute during a hand of poker.
 * When a player raises, they increase the current bet to a new amount, which must be higher than the current bet. 
 * The player must have enough money to cover the new bet amount.
 */
public class RaiseCommand extends Command {

    protected int _targetBet;
    

    public RaiseCommand() {}

    /**
     * Constructor for the RaiseCommand class.
     * @param p the player who is raising
     * @param newBet the new bet amount that the player wants to raise to
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public RaiseCommand(IPokerActions p, int newBet, int money, int pocketMoney) {
        super(p, money, pocketMoney);
        _targetBet = newBet;
    }
    
    /*
        The target bet must be higher than the current maximum bet and the player must have enough money to cover the new bet amount.
        * @param onBet the current bet amount that the player has already bet in the current hand.
        * @param totalMoney the total amount of money that the player has not bet yet.
        * @param maxBet the current maximum bet that any player has bet in the current hand.
        * @return true if the raise is valid, false otherwise.
    @Override
    public boolean validate(final int onBet, final int totalMoney, final int maxBet){
        return _targetBet <= onBet + totalMoney && _targetBet > maxBet;
    }
    */

    @Override
    public void requireParameters(InputStream in) throws IOException {
        
        int quantity = SocketUtils.receiveInt(in);
        _targetBet = quantity;
    }

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

        if (_targetBet == _playersOffBetMoney + _playersOnBetMoney) {
            AllInCommand command = new AllInCommand(_player, _playersOffBetMoney, _playersOnBetMoney);
            return command.execute(sb, bb, maxBet);
        }

        _player.raise(_targetBet);
        return CommandResult.continuePlaying(_targetBet, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandName() {
        return "RAISE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandText() {
        return "raise";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandTextShotcut() {
        return "r";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "Increase the current bet to a new amount.";
    }

    @Override
    protected int getCommandNetworkCode() {
        return GameType.RAISE_ACTION;
    }

}
