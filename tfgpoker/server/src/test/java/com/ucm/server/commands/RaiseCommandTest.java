package com.ucm.server.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;

public class RaiseCommandTest {
    
    private static final int SMALL_BLIND = 1;
    private static final int BIG_BLIND = SMALL_BLIND * 2;
    private static final int INITIAL_MONEY = 1000;

    @Test
    public void badFormat(){

        final int initialBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raize 100";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        

        assertEquals(command, null);
    }

    @Test
    public void notParameter(){

        final int initialBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        

        assertEquals(command, null);
    }

    @Test
    public void notNumberParameter(){

        final int initialBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise one hundred";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);


        assertEquals(command, null);
    }

    @Test
    public void negativeParameter(){
        
        final int initialBet = 0;
        final int maxBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise -100";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        boolean valid = command.validate(maxBet);


        assertNotEquals(command, null);
        assertEquals(false, valid);
    }

    @Test
    public void correctParameterAndLessThanMaxBet(){
        
        final int initialBet = 0;
        final int maxBet = 100;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise 50";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        boolean valid = command.validate(maxBet);


        assertNotEquals(command, null);
        assertEquals(false, valid);
    }

    @Test
    public void correctParameterAndGreaterThanMaxBetFromZero(){
        
        final int initialBet = 0;
        final int maxBet = 100;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise 150";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        boolean valid = command.validate(maxBet);
        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);


        assertNotEquals(command, null);
        assertEquals(true, valid);
        assertEquals(CommandResult.continuePlaying(150, true), result);
        assertEquals(150, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY - 150, player.getMoneyOffBet());
    }

    @Test
    public void correctParameterAndGreaterThanMaxBetFromSomeMoney(){
        
        final int initialBet = 50;
        final int maxBet = 100;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise 150";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        boolean valid = command.validate(maxBet);
        CommandResult result = command.execute(SMALL_BLIND, BIG_BLIND, maxBet);


        assertNotEquals(command, null);
        assertEquals(true, valid);
        assertEquals(CommandResult.continuePlaying(150, true), result);
        assertEquals(150, player.getMoneyOnBet());
        assertEquals(INITIAL_MONEY - 150, player.getMoneyOffBet());
    }

    @Test
    public void correctParametersButGreaterThanPlayerTotalMoneyFromZero(){
        
        final int initialBet = 0;
        final int maxBet = 100;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "raise 1500";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        boolean valid = command.validate(maxBet);


        assertNotEquals(command, null);
        assertNotEquals(true, valid);
    }

}