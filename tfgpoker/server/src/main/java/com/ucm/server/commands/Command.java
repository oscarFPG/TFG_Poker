package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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

    private static final Logger log = LogManager.getLogger(Command.class);

    protected IPokerActions _player;
    protected int _playersOffBetMoney;
    protected int _playersOnBetMoney;
    protected int _currentHandBet;

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
     * @param money       the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the
     *                    current hand.
     */
    public Command(IPokerActions p, int money, int pocketMoney) {
        _player = p;
        _playersOffBetMoney = money;
        _playersOnBetMoney = pocketMoney;
        _currentHandBet = 0;
    }


    public static String showAvailableCommands() {

        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (Command command : AVAILABLE_COMMANDS) {
            if (i == AVAILABLE_COMMANDS.size())
                sb.append("\t> " + command.getCommandName() + " " + command.getCommandSyntax() + " : "
                        + command.getCommandDescription());
            else
                sb.append("\t> " + command.getCommandName() + " " + command.getCommandSyntax() + " : "
                        + command.getCommandDescription()).append("\n");
            i++;
        }

        return sb.toString();
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
    public static Command parseCommand(final int commandNetworkCode, IPokerActions p) {

        for (Command command : AVAILABLE_COMMANDS) {
            if (command.matchCommand(commandNetworkCode)) {
                command._player = p;
                return command;
            }
        }

        return null;
    }

    /**
     * Method that receives the current bet value and updates the command's current
     * bet accordingly.
     * This method allows the command to keep track of the current bet in the game,
     * which can be used for validation and execution of the command.
     * 
     * @param currentBet the current bet value in the game.
     */
    public void receiveCurrentBet(int currentBet) {
        _currentHandBet = currentBet;
    }

    /**
     * Method that returns the syntax of the command, which includes the command
     * text and its shortcut.
     * 
     * @return a string representing the syntax of the command, including the
     *         command text and its shortcut.
     */
    public final String getCommandSyntax() {
        return "(" + getCommandText() + "/" + getCommandTextShotcut() + ")";
    }

    /**
     * Method that checks if the input command matches the specific command.
     * 
     * @param command the input command string to be checked against the specific
     *                command.
     * @return true if the input command matches the specific command, false
     *         otherwise.
     */
    public final boolean matchCommand(final int code) {
        return this.getCommandNetworkCode() == code;
    }

    /**
     * Ask for the parameters needed by the command if necessary
     * @param in input to receive the parameters by the user
     * @return true if the operation was succesful, false otherwise
     */
    public void requireParameters(InputStream in) throws IOException {
        // Not necesary for most of commands !!!
    }

    /**
     * Allows to request additional parameters to the poker player to create a full defined action by the player
     */
    public abstract void requestParameters();

    /**
     * Checks if the command can be executed correctly based on the context
     * 
     * @return true if the command can be executed, false in any other case
     */
    public abstract boolean validate();

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
    public abstract String getCommandText();

    /**
     * 
     * @return
     */
    public abstract String getCommandTextShotcut();

    /**
     * Method that returns a description of the command, including its syntax and
     * usage.
     * 
     * @return a string representing the description of the command
     */
    public abstract String getCommandDescription();

    /**
     * 
     * @return
     */
    protected abstract int getCommandNetworkCode();

}