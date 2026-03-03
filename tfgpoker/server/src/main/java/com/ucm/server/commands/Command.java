package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;
import java.util.Arrays;
import java.util.List;


/**
 * Abstract class that represents a command in the poker game. This class serves as a base for specific commands such as fold, check, call, raise, and all-in. 
 * It defines the common attributes and methods that all commands should have, including the player associated with the command, the money involved, and the current bet.
 * Each specific command will implement the execute and checkCommand methods to perform the corresponding actions in the game.
 */
public abstract class Command {
 

    protected Player _player;
    protected int _money;
    protected int _pocketMoney;
    protected int _currentBet;

    
    private static final List<Command> AVAILABLE_COMMANDS = Arrays.asList(
        new CallCommand(),
        new CheckCommand(),
        new FoldCommand(),
        new RaiseCommand(),
        new AllInCommand()
    );
    

    public Command(){};

    /**
     * Constructor method that creates a Command object with the specified player, money, and pocket money values. This constructor initializes the command with the given parameters and sets the current bet to 0.
     * @param p the player associated with the command.
     * @param money the total amount of money that the player has not bet yet.
     * @param pocketMoney the amount of money that the player has already bet in the current hand.
     */
    public Command(Player p, int money, int pocketMoney){
        _player = p;
        _money = money;
        _pocketMoney = pocketMoney;
        _currentBet = 0;
    }


    public static String showAvailableCommands(){
        
        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (Command command : AVAILABLE_COMMANDS) {
            if(i == AVAILABLE_COMMANDS.size())
                sb.append( "\t> " + command.getCommandName() );
            else
                sb.append( "\t> " + command.getCommandName() ).append("\n");
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
     * @param input
     * @param p
     * @return
     */
    public static Command parseCommand(String[] input, Player p) {

        for (Command command : AVAILABLE_COMMANDS ){
            if( command.matchCommand(input[0]) && command.checkAttributes(input)){  // Must be called in this particular order
                return command.create(input, p);
            }
        }
        
        return null;
    }
    
    /**
     * Method that receives the current bet value and updates the command's current bet accordingly. 
     * This method allows the command to keep track of the current bet in the game, which can be used for validation and execution of the command.
     * @param currentBet the current bet value in the game.
     */
    public void receiveCurrentBet(int currentBet){
        _currentBet = currentBet;
    }

    /**
     * Method that executes the command. This method should be implemented by each specific command to perform the corresponding action in the game.
     * @param sb the small blind value.
     * @param bb the big blind value.
     * @param maxBet the maximum bet value in the current round.
     * @return a CommandResult object indicating that the player has stopped playing.
     */
    public abstract CommandResult execute(final int sb, final int bb, final int maxBet);

    /**
     * Method that returns the name of the command.
     * @return a string representing the name of the command.
     */
    public abstract String getCommandName();

    /**
     * Method that returns a description of the command, including its syntax and usage.
     * @return a string representing the description of the command
     */
    public abstract String getCommandDescription();

    /**
     * Method that checks if the input command matches the specific command.
     * @param command the input command string to be checked against the specific command.
     * @return true if the input command matches the specific command, false otherwise.
     */
    public abstract boolean matchCommand(String command);

    /**
     * Method that checks if the attributes of the command are valid based on the input command.
     * @param fullCommand the input command array containing the command and its attributes.
     * @return true if the attributes of the command are valid, false otherwise.
     */
    public abstract boolean checkAttributes(String[] fullCommand);

    /**
     * Method that creates a new instance of the specific command based on the input command and the player.
     * @param fullCommand the input command array containing the command and its attributes.
     * @param p the player associated with the command.
     * @return a new instance of the specific command if the input command is valid, null otherwise.
     */
    public abstract Command create(String[] fullCommand, Player p) ;
 
    
}