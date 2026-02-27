package com.ucm.server.middleclasses;


public record CommandResult(int bet, boolean raises, boolean folds) {

    public static CommandResult continuePlaying(int money, boolean raise) {
        return new CommandResult(money, raise, false);
    }

    public static CommandResult stopPlaying(int money) {
        return new CommandResult(money, false, true);
    }
}