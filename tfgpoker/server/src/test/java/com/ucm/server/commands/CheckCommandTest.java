package com.ucm.server.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.middleclasses.CommandResult;


public class CheckCommandTest {
    
    private static final int SMALL_BLIND = 1;
    private static final int BIG_BLIND = SMALL_BLIND * 2;
    private static final int INITIAL_MONEY = 1000;

    @Test
    public void notAllowBadFormat() {

        final int initialBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "Chek";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);


        assertEquals(command, null);
    }

    @Test
    public void fromZeroWithoutPreviousBet() {

        final int initialBet = 0;
        final int maxBet = 0;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "check";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(CommandResult.continuePlaying(0, false), result);
        assertEquals(0, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY, player.getMoneyOffBet());
    }

    @Test
    public void fromSomeMoney() {

        final int initialBet = 200;
        final int maxBet = initialBet;

        FakePlayer player = new FakePlayer(0, INITIAL_MONEY - initialBet, initialBet);
        String input = "check";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);

        assertEquals(initialBet, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY - initialBet, player.getMoneyOffBet());
    }

}