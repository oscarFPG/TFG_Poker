package com.ucm.server.players;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.ServerMain;
import com.ucm.server.exceptions.TurnTimeoutException;
import com.ucm.server.gameobjects.Player;

public class TestGeminiBot {


    // ---------------------- TEST 1: PREFLOP ----------------------

    //@Test
    public void testPreflopDecision() throws IOException {

        GeminiLLM geminiLLM = new GeminiLLM();
        Player player = new Player(0, "Gemini", 1000, geminiLLM);

        player.receiveRole(PlayerRole.DEALER);
        player.receiveCard(new Card(1, Suit.HEARTS)); // Ah
        player.receiveCard(new Card(13,Suit.DIAMONDS)); // Kd

        player.notifyEquity(0.65);

        String action;
        try {
            action = player.makePlay(5, 10, 10);
            
            System.out.println("Preflop action: " + action);

            assertValidAction(action);
        } catch (TurnTimeoutException e) {
            e.printStackTrace();
        }


    }

    // ---------------------- TEST 2: FLOP ----------------------

    //@Test
    public void testFlopDecision() throws IOException {

        GeminiLLM geminiLLM = new GeminiLLM();
        Player player = new Player(0, "Gemini", 1000, geminiLLM);

        player.receiveRole(PlayerRole.CUT_OFF);
        player.receiveCard(new Card(10, Suit.HEARTS)); // Th
        player.receiveCard(new Card(10, Suit.DIAMONDS)); // Td

        player.receiveTableCard(new Card(10, Suit.CLUBS)); // set
        player.receiveTableCard(new Card(5, Suit.SPADES));
        player.receiveTableCard(new Card(2, Suit.HEARTS));

        player.notifyEquity(0.85);

        String action;
        try {
            action = player.makePlay(5, 10, 20);

            System.out.println("Flop action: " + action);

            assertValidAction(action);
        } catch (TurnTimeoutException e) {
            e.printStackTrace();
        }

        
    }

    // ---------------------- TEST 3: LOW EQUITY ----------------------

    //@Test
    public void testLowEquity() throws IOException {

        GeminiLLM geminiLLM = new GeminiLLM();
        Player player = new Player(0, "Gemini", 1000, geminiLLM);

        player.receiveRole(PlayerRole.BIG_BLIND);
        player.receiveCard(new Card(2, Suit.HEARTS));
        player.receiveCard(new Card(7, Suit.DIAMONDS));

        player.receiveTableCard(new Card(14, Suit.CLUBS));
        player.receiveTableCard(new Card(13,Suit.SPADES));
        player.receiveTableCard(new Card(12, Suit.HEARTS));

        player.notifyEquity(0.05);

        String action;
        try {
            action = player.makePlay(5, 10, 20);

            System.out.println("Low equity action: " + action);

            assertValidAction(action);
        } catch (TurnTimeoutException e) {
            e.printStackTrace();
        }

        
    }

  
    private void assertValidAction(String action) {

        assertNotNull(action);

        action = action.toLowerCase().trim();

        boolean valid =
                action.equals("fold") ||
                action.equals("call") ||
                action.equals("check") ||
                action.matches("raise \\d+(\\.\\d+)?");

        assertTrue(valid, "Invalid action: " + action);
    }
    
}