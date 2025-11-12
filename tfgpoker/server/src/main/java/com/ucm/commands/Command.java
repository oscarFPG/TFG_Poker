package com.ucm.server.commands;

import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.CommandResult;


public abstract class Command {
 
    // TODO : Emplezar interfaz para relacionar Player y Command
    // TODO : Solo con metodos -> fold(), check(), call(), raise(), allIn(), increasePocketMoney() 
    // TODO : Sustituir el atributo y el parametro del constructor
    protected Player _player;
    protected int _money;
    
    
    public Command(Player p, int money){
        _player = p;
        _money = money;
    }
    
    public abstract CommandResult execute(final int sb, final int bb, final int maxBet);
    
}