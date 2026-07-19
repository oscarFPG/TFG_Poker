package com.ucm.server.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.middleclasses.CommandResult;



public class AllInCommandTest {

    private static final int SMALL_BLIND = 1;
    private static final int BIG_BLIND = SMALL_BLIND * 2;
    private static final int INITIAL_MONEY = 1000;
    

    @Test
    public void badFormat() {

        final int initialBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "alIin";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        assertEquals(command, null);
    }

    @Test
    public void fromZero() {

        final int initialBet = 0;
        int maxBet = 0;


        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "all-in";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(INITIAL_MONEY, true), result);
        assertEquals(INITIAL_MONEY, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
    }

    @Test
    public void fromSome() {

        final int initialBet = 200;
        int maxBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "all-in";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(INITIAL_MONEY, true), result);
        assertEquals(INITIAL_MONEY, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
    }

    @Test
    public void fromZero_previousBet() {

        final int initialBet = 0;
        int maxBet = 20;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "all-in";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(INITIAL_MONEY, true), result);
        assertEquals(INITIAL_MONEY, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
    }

    @Test
    public void fromSome_previousBet() {

        final int initialBet = 200;
        int maxBet = 20;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "all-in";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(INITIAL_MONEY, true), result);
        assertEquals(INITIAL_MONEY, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
    }

    @Test
    public void fromZero_previousAllIn() {

        final int initialBet = 0;
        int maxBet = INITIAL_MONEY;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "all-in";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(INITIAL_MONEY, false), result);
        assertEquals(INITIAL_MONEY, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
    }

    @Test
    public void fromZome_previousAllIn() {

        final int initialBet = 100;
        int maxBet = INITIAL_MONEY;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "all-in";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(INITIAL_MONEY, false), result);
        assertEquals(INITIAL_MONEY, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
    }

}