package com.ucm.server.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.middleclasses.CommandResult;

public class FoldCommandTest {
    
    private static final int SMALL_BLIND = 1;
    private static final int BIG_BLIND = SMALL_BLIND * 2;
    private static final int INITIAL_MONEY = 1000;

    @Test
    public void badFormat(){

        final int initialBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "fodl";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        assertEquals(command, null);
    }

    @Test
    public void fromZero(){

        final int initialBet = 0;
        int maxBet = 0;
        
        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "fold";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.stopPlaying(), result);
        assertEquals(0, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY, player.getMoneyOffBet());
        assertEquals(true, player.isFolded());
    }

    @Test
    public void fromSomeMoney(){
    
        final int initialBet = 100;
        int maxBet = 200;
        
        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "fold";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.stopPlaying(), result);
        assertEquals(initialBet, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY - initialBet, player.getMoneyOffBet());
        assertEquals(true, player.isFolded());
    }

}
