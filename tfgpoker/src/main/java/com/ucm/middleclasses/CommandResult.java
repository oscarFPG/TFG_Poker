package com.ucm.middleclasses;


public record CommandResult(int bet, boolean folds, boolean raises) {
    
    
    public CommandResult continuePlaying(int b, boolean r){
        return new CommandResult(b, false, r);
    }
    
    public CommandResult stopPlaying(int b){
        return new CommandResult(b, true, false);
    }
}
