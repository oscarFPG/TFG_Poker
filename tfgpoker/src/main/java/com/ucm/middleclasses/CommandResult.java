package com.ucm.middleclasses;


public record CommandResult(int bet, boolean raises) {

    public static CommandResult continuePlaying(int money, boolean raise) {
        return new CommandResult(money, raise);
    }

    public static CommandResult stopPlaying(int money) {
        return new CommandResult(money, false);
    }
}
