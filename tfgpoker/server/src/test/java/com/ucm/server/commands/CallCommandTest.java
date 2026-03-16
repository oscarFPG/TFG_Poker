package com.ucm.server.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.middleclasses.CommandResult;


public class CallCommandTest {
    
    private static final int INITIAL_MONEY = 1000;


    @Test
    public void badFormat(){

        final int initialBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "Cad";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        Assertions.assertEquals(command, null);
    }

    @Test
    public void badFormat2(){

        final int initialBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = " c";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        Assertions.assertEquals(command, null);
    }

    @Test
    public void correctFormatMinusAndMayus(){

        final int initialBet = 0;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "CalL";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);
        
        Assertions.assertNotEquals(command, null);
    }

    @Test
    public void correctCallNoPreviousBet(){
        
        final int initialBet = 0;
        int maxBet = 200;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "call";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        boolean valid = command.validate(maxBet);
        CommandResult result = command.execute(1, 2, maxBet);

        Assertions.assertEquals(200, result.bet());
        Assertions.assertEquals(true, valid);
        Assertions.assertEquals(200, player.getMoneyOnBet());
        Assertions.assertEquals(INITIAL_MONEY - 200, player.getMoneyOffBet());
    }

    @Test
    public void correctCallWithPreviousBet(){
        
        final int initialBet = 100;
        int maxBet = 200;

        IPokerActions player = new FakePlayer(INITIAL_MONEY - initialBet, initialBet);
        String input = "call";
        String[] inputFormatted = input.split(" ");
        Command command = Command.parseCommand(inputFormatted, player);

        boolean valid = command.validate(maxBet);
        CommandResult result = command.execute(1, 2, maxBet);

        Assertions.assertEquals(200, result.bet());
        Assertions.assertEquals(true, valid);
        Assertions.assertEquals(200, player.getMoneyOnBet());
        Assertions.assertEquals(INITIAL_MONEY - 200, player.getMoneyOffBet());
    }

}