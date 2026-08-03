package com.ucm.server.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.ucm.server.FakePlayer;
import com.ucm.server.middleclasses.CommandResult;


public class ConvertCommandsTest {
    
    public static final int PLAYER_ID = 0, OFF_BET = 1000, ON_BET = 0;
    public static final int SB = 1, BB = 2 * SB; 

    /* ------------------ CALL ------------------ */
    @Test
    public void CallToCheck() {

        final int MAX_BET = 0;
        String input = "call";
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(0, player.getMoneyOnBet());
        assertEquals(0, result.bet());
    }


    /* ------------------ CHECK ------------------ */
    @Test
    public void CheckToCall() {
        
        final int MAX_BET = BB;
        String input = "check";
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(MAX_BET, player.getMoneyOnBet());
        assertEquals(CommandResult.continuePlaying(MAX_BET, false), result);
        assertEquals(MAX_BET, result.bet());
    }


    /* ------------------ RAISE ------------------ */
    @Test
    public void RaiseToCall_NoBet() {
        
        final int MAX_BET = BB;
        String input = "raise " + String.valueOf(MAX_BET);
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(MAX_BET, player.getMoneyOnBet());
        assertEquals(MAX_BET, result.bet());
        assertEquals(CommandResult.continuePlaying(MAX_BET, false), result);
    }

    @Test
    public void RaiseToCall_WithBet() throws IOException {
        
        final int MAX_BET = 2*BB;
        String input = "raise " + String.valueOf(MAX_BET);
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        player.putBigBlindBet(BB);

        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(MAX_BET, player.getMoneyOnBet());
        assertEquals(OFF_BET - MAX_BET, player.getMoneyOffBet());
        assertEquals(MAX_BET, result.bet());
        assertEquals(CommandResult.continuePlaying(MAX_BET, false), result);
    }

    @Test
    public void RaiseToAllIn_NoBet() {
        
        final int MAX_BET = OFF_BET;
        String input = "raise " + String.valueOf(OFF_BET);
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(OFF_BET, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
        assertEquals(CommandResult.continuePlaying(MAX_BET, false), result);
        assertEquals(OFF_BET, result.bet());
    }

    @Test
    public void RaiseToAllIn_WithBet() throws IOException {
        
        final int MAX_BET = OFF_BET;
        String input = "raise " + String.valueOf(OFF_BET);
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        player.putBigBlindBet(BB);
        
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(OFF_BET, player.getMoneyOnBet());
        assertEquals(0, player.getMoneyOffBet());
        assertEquals(CommandResult.continuePlaying(MAX_BET, false), result);
        assertEquals(OFF_BET, result.bet());
    }

    @Test
    public void RaiseToCheck() {
        
        final int MAX_BET = 0;
        String input = "raise 0";
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);

        assertEquals(0, player.getMoneyOnBet());
        assertEquals(0, result.bet());
        assertEquals(CommandResult.continuePlaying(0, false), result);
    }

    @Test
    public void RaiseToCheckToCall() {
        
        final int MAX_BET = BB;
        String input = "raise 0";
        String[] inputFormatted = input.split(" ");

        FakePlayer player = new FakePlayer(PLAYER_ID, OFF_BET, ON_BET);
        Command command = Command.parseCommand(inputFormatted, player);
        CommandResult result = command.execute(SB, BB, MAX_BET);
    
        assertEquals(MAX_BET, player.getMoneyOnBet());
        assertEquals(MAX_BET, result.bet());
        assertEquals(CommandResult.continuePlaying(MAX_BET, false), result);
    }

}