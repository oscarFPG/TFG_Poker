package com.ucm.server.commands;

import java.util.Arrays;
import java.util.List;


import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.middleclasses.CommandResult;


/**
 * Abstract class that represents a command in the poker game. This class serves
 * as a base for specific commands such as fold, check, call, raise, and all-in.
 * It defines the common attributes and methods that all commands should have,
 * including the player associated with the command, the money involved, and the
 * current bet.
 * Each specific command will implement the execute and checkCommand methods to
 * perform the corresponding actions in the game.
 */
public abstract class Command {

    protected IPlayerActions _player;
    protected int _playersOffBetMoney;
    protected int _playersOnBetMoney;

    private static final List<Command> AVAILABLE_COMMANDS = Arrays.asList(
            new CallCommand(),
            new CheckCommand(),
            new FoldCommand(),
            new RaiseCommand(),
            new AllInCommand()
    );


    /**
     * Constructor to create a empty model of any command
     */
    public Command() {}

    /**
     * Constructor method that creates a Command object with the specified player,
     * money, and pocket money values. This constructor initializes the command with
     * the given parameters and sets the current bet to 0.
     * 
     * @param p           the player associated with the command.
     * @param offBetMoney the total amount of money that the player has not bet yet.
     * @param onBetMoney the amount of money that the player has already bet in the
     *                   current hand.
     */
    public Command(IPlayerActions p) {
        _player = p;
        _playersOffBetMoney = p.getMoneyOffBet();
        _playersOnBetMoney = p.getMoneyOnBet();
    }

    /**
     * AllInCommand: "all-in" or "a"
     * FoldCommand: "fold" or "f"
     * CheckCommand: "check" or "k"
     * CallCommand: "call" or "c"
     * RaiseCommand: "raise <amount>" or "r <amount>"
     * 
     * @param input to identify a command
     * @param p player that is making the command
     * @return the command identified by the input, null if the input does not match any command
     */
    public static Command parseCommand(final String[] input, IPlayerActions p) {

        for (Command command : AVAILABLE_COMMANDS) {
            if (command.matchCommand(input[0])) {
                return command.createCommand(input, p);
            }
        }

        return null;
    }

    /**
     * Method that checks if the input command matches the specific command.
     * 
     * @param command the input command string to be checked against the specific
     *                command.
     * @return true if the input command matches the specific command, false
     *         otherwise.
     */
    protected final boolean matchCommand(final String input) {
        return getCommandFormat().equalsIgnoreCase(input) ||
               getCommandFormatShortcut().equalsIgnoreCase(input);
    }

    /**
     * Method that returns the syntax of the command, which includes the command
     * text and its shortcut.
     * 
     * @return a string representing the syntax of the command, including the
     *         command text and its shortcut.
     */
    public final String getCommandSyntax() {
        return getCommandFormat() + "/" + getCommandFormatShortcut() + getCommandParameters();
    }

    public String getCommandParameters(){
        return "";
    }


    protected abstract Command createCommand(final String[] commandFormat, final IPlayerActions player);

    /**
     * Checks if the command can be executed correctly based on the context
     * 
     * @return true if the command can be executed, false in any other case
     */
    //public abstract boolean validate(final int maxBet);

    /**
     * Method that executes the command. This method should be implemented by each
     * specific command to perform the corresponding action in the game.
     * 
     * @param sb     the small blind value.
     * @param bb     the big blind value.
     * @param maxBet the maximum bet value in the current round.
     * @return a CommandResult object indicating that the player has stopped
     *         playing.
     */
    public abstract CommandResult execute(final int sb, final int bb, final int maxBet);

    /**
     * Method that returns the name of the command.
     * 
     * @return a string representing the name of the command.
     */
    public abstract String getCommandName();

    /**
     * 
     * @return
     */
    public abstract String getCommandFormat();

    /**
     * 
     * @return
     */
    public abstract String getCommandFormatShortcut();

    /**
     * Method that returns a description of the command, including its syntax and
     * usage.
     * 
     * @return a string representing the description of the command
     */
    public abstract String getCommandDescription();


}