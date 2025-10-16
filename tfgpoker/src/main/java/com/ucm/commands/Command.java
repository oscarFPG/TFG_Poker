package com.ucm.commands;

import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.CommandResult;


public abstract class Command {
 
    protected Player _player;
    protected int _betMoney;
    
    
    public Command(Player p, int value){
        _player = p;
        _betMoney = value;
    }
    
    
    public abstract CommandResult execute(final int sb, final int bb, final int maxBet);
    
}