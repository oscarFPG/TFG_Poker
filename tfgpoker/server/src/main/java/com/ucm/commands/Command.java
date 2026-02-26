package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;

/**
 * Abstract class that represents a command in the poker game. This class serves as a base for specific commands such as fold, check, call, raise, and all-in. 
 * It defines the common attributes and methods that all commands should have, including the player associated with the command, the money involved, and the current bet.
 * Each specific command will implement the execute and checkCommand methods to perform the corresponding actions in the game.
 */
public abstract class Command {
 
    // TODO : Emplezar interfaz para relacionar Player y Command
    // TODO : Solo con metodos -> fold(), check(), call(), raise(), allIn(), increasePocketMoney() 
    // TODO : Sustituir el atributo y el parametro del constructor
    protected Player _player;
    protected int _money;
    protected int _pocketMoney;
    protected int _currentBet;
    protected String _name;
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
        _name = getCommandName();
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
     * Method that checks if the command can be executed. This method should be implemented by each specific command 
     * to determine if the player can perform the action based on the game state and rules.
     * @return true if the command can be executed, false otherwise.
     */
    public abstract boolean checkCommand();
    /**
     * Method that returns the name of the command.
     * @return a string representing the name of the command.
     */
    public abstract String getCommandName();
}