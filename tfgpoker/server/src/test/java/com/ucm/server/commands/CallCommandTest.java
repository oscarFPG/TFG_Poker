package com.ucm.server.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.middleclasses.CommandResult;


public class CallCommandTest {
    
    private static final int SMALL_BLIND = 1;
    private static final int BIG_BLIND = SMALL_BLIND * 2;
    private static final int INITIAL_MONEY = 1000;


    @Test
    public void NotAllowBadFormat() {

        final int initialBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "Cad";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        assertEquals(command, null);
    }

    @Test
    public void permitBlankSpacesWithGoodFormat() {

        final int initialBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = " c";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        assertEquals(command, null);
    }

    @Test
    public void correctFormatMinusAndMayus() {

        final int initialBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "CalL";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        assertNotEquals(command, null);
    }

    @Test
    public void correctCallNoPreviousBet() {
        
        final int initialBet = 0;
        int maxBet = 200;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "call";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(200, false), result);
        assertEquals(200, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY - 200, player.getMoneyOffBet());
    }

    @Test
    public void correctCallWithPreviousBet() {
        
        final int initialBet = 100;
        int maxBet = 200;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "call";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(200, false), result);
        assertEquals(200, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY - 200, player.getMoneyOffBet());
    }

}