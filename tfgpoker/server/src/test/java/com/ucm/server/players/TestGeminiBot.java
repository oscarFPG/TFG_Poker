package com.ucm.server.players;

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

public class TestGeminiBot {

    private static String apiKey;

    @BeforeAll
    static void loadApiKey() throws Exception {
        InputStream input = ServerMain.class
                .getClassLoader()
                .getResourceAsStream("credentials.json");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> json = mapper.readValue(input, Map.class);
        apiKey = json.get("GEMINI_API_KEY");
    }

    // ---------------------- TEST 1: PREFLOP ----------------------

    @Test
    public void testPreflopDecision() {

        GeminiLLM bot = new GeminiLLM(0, 1000, apiKey);

        bot.notifyPlayerRole(PlayerRole.DEALER);
        bot.notifyPlayerCard(new Card(1, Suit.HEARTS)); // Ah
        bot.notifyPlayerCard(new Card(13,Suit.DIAMONDS)); // Kd

        bot.notifyEquity(0.65);

        String action = bot.actionMakePlay(5, 10, 10);

        System.out.println("Preflop action: " + action);

        assertValidAction(action);
    }

    // ---------------------- TEST 2: FLOP ----------------------

    @Test
    public void testFlopDecision() {

        GeminiLLM bot = new GeminiLLM(0, 1000, apiKey);

        bot.notifyPlayerRole(PlayerRole.CUT_OFF);
        bot.notifyPlayerCard(new Card(10, Suit.HEARTS)); // Th
        bot.notifyPlayerCard(new Card(10, Suit.DIAMONDS)); // Td

        bot.notifyTableCard(new Card(10, Suit.CLUBS)); // set
        bot.notifyTableCard(new Card(5, Suit.SPADES));
        bot.notifyTableCard(new Card(2, Suit.HEARTS));

        bot.notifyEquity(0.85);

        bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN, "raise", 10);
        bot.notifyPlayerAction(PlayerRole.MIDDLE_POSITION, "call", 10);

        String action = bot.actionMakePlay(5, 10, 20);

        System.out.println("Flop action: " + action);

        assertValidAction(action);
    }

    // ---------------------- TEST 3: LOW EQUITY ----------------------

    @Test
    public void testLowEquity() {

        GeminiLLM bot = new GeminiLLM(0, 1000, apiKey);

        bot.notifyPlayerRole(PlayerRole.BIG_BLIND);
        bot.notifyPlayerCard(new Card(2, Suit.HEARTS));
        bot.notifyPlayerCard(new Card(7, Suit.DIAMONDS));

        bot.notifyTableCard(new Card(14, Suit.CLUBS));
        bot.notifyTableCard(new Card(13,Suit.SPADES));
        bot.notifyTableCard(new Card(12, Suit.HEARTS));

        bot.notifyEquity(0.05);

        String action = bot.actionMakePlay(5, 10, 20);

        System.out.println("Low equity action: " + action);

        assertValidAction(action);
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