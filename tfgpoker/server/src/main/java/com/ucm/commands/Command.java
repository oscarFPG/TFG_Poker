package com.ucm.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.ucm.GameType;
import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public abstract class Command {
 
    // TODO : Emplezar interfaz para relacionar Player y Command
    // TODO : Solo con metodos -> fold(), check(), call(), raise(), allIn(), increasePocketMoney() 
    // TODO : Sustituir el atributo y el parametro del constructor
    protected Player _player;
    protected int _money;
    protected int _pocketMoney;
    protected int _currentBet;
    protected String _name;

    private static final List<Command> AVAILABLE_COMMANDS = Arrays.asList(
        new CallCommand(),
        new CheckCommand(),
        new FoldCommand(),
        new RaiseCommand(),
        new AllInCommand()
    );
    
    public Command(){};

    public Command(Player p, int money, int pocketMoney){
        _player = p;
        _money = money;
        _pocketMoney = pocketMoney;
        _currentBet = 0;
        _name = getCommandName();
    }
    
    public static Command parse (int codePlay, Player p) throws IOException{
        for (Command command : AVAILABLE_COMMANDS ){
            if(command.matchCommand(codePlay)){
                return command.create(codePlay, p);
            }
        }
        //lanzar CommandParseexception(Messages.UNKNOWN_COMMAND);
        return null;//cambiar
    }
    
    public void receiveCurrentBet(int currentBet){
        _currentBet = currentBet;
    }

    public abstract CommandResult execute(final int sb, final int bb, final int maxBet);
    
    public abstract boolean checkCommand();

    public abstract String getCommandName();

    protected abstract int getCommandIdentifier();

    protected abstract boolean correctCode(int codePlay);

    public boolean matchCommand(int codePlay){
        int CommandId = getCommandIdentifier();
        return codePlay == CommandId;
    }

    public abstract Command create(int codePlay, Player p) throws IOException;

 
    
}