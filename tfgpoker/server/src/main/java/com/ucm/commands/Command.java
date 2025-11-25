package com.ucm.commands;

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
    
    public Command(Player p, int money, int pocketMoney){
        _player = p;
        _money = money;
        _pocketMoney = pocketMoney;
        _currentBet = 0;
        _name = getCommandName();
    }
    
    public void receiveCurrentBet(int currentBet){
        _currentBet = currentBet;
    }

    public abstract CommandResult execute(final int sb, final int bb, final int maxBet);
    
    public abstract boolean checkCommand();

    public abstract String getCommandName();
}