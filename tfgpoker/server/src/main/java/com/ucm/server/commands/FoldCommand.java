package com.ucm.server.commands;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Class that represents the fold command in the poker game. This command allows a player to fold and stop playing in the current hand.
 */
public class FoldCommand extends Command {

    public FoldCommand(){
        super();
    }

    /**
     * Constructor method that creates a FoldCommand.
     * @param p the player that is making the fold play.
     */
    public FoldCommand(Player p) {
        super(p, 0, 0);
    }

    /**
     * Player can always fold if it has money available(It is not eliminated)
     */
    @Override
    public boolean validate(final int onBet, final int totalMoney, final int maxBet){
        return totalMoney > 0;
    }
    
    @Override
    public CommandResult execute(int sb, int bb, int maxBet) {

        /*
         * Player always can fold, no need to call the checkCommand() method
         */
        _player.fold();
        return CommandResult.stopPlaying(0);
    }

    @Override
    public String getCommandName() {
        return "FOLD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandText() {
        return "fold";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandTextShotcut() {
        return "f";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandDescription() {
        return "Retire from the current hand. If you have already bet, you will lose the money.";
    }

    @Override
    public boolean checkAttributes(String[] fullCommand) {
        return fullCommand.length == 1;
    }

    @Override
    protected int getCommandNetworkCode() {
        return GameType.FOLD_ACTION;
    }

}
