package com.ucm.server.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Deck;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.middleclasses.HandInfo;

public class EquityCalculatorTest {

    private static final int ITERATIONS = 5;
    private static final Random RANDOM = new Random();

    @Test
    public void testMultipleRandomScenarios() throws Exception {

        Evaluator.getInstance();

        for (int i = 0; i < ITERATIONS; i++) {

            int numPlayers = 2 + RANDOM.nextInt(7);

            System.out.println("\n==============================");
            System.out.println("SCENARIO " + (i + 1));
            System.out.println("==============================");

            runFullGameSimulation(numPlayers);
        }
    }

    private void runFullGameSimulation(int numPlayers) {

        Deck deck = new Deck();
        List<HandInfo> players = new ArrayList<>();

        System.out.println("\n--- PLAYERS ---");

        for (int i = 0; i < numPlayers; i++) {

            Card c1 = deck.takeRandomCard();
            Card c2 = deck.takeRandomCard();

            players.add(new HandInfo(i, new Card[]{c1, c2}));

            System.out.printf("Player %d : [%s %s]%n",
                    i,
                    c1.toLetterString(),
                    c2.toLetterString());
        }

        Card[] table = new Card[5];

        System.out.println("\n--- PREFLOP ---");
        assertEquity(players, table, deck);

        dealCard(table, deck, 0);
        dealCard(table, deck, 1);
        dealCard(table, deck, 2);

        System.out.println("\n--- FLOP ---");
        printTable(table);
        assertEquity(players, table, deck);

        dealCard(table, deck, 3);

        System.out.println("\n--- TURN ---");
        printTable(table);
        assertEquity(players, table, deck);

        dealCard(table, deck, 4);

        System.out.println("\n--- RIVER ---");
        printTable(table);
        assertEquity(players, table, deck);
    }

    private void assertEquity(
            List<HandInfo> players,
            Card[] table,
            Deck deck) {

        Map<Integer, Double> equity =
                EquityCalculator.calculateEquity(players, table, deck);

        assertEquals(players.size(), equity.size());

        for (HandInfo player : players) {

            assertTrue(
                    equity.containsKey(player.playerID()),
                    "Missing equity for player " + player.playerID());

            double eq = equity.get(player.playerID());

            System.out.printf(
                    "Player %d : %.2f%%%n",
                    player.playerID(),
                    eq * 100);

            assertTrue(
                    eq >= 0.0 && eq <= 1.0,
                    "Invalid equity for player "
                            + player.playerID()
                            + ": "
                            + eq);
        }

        System.out.println();
    }

    private void dealCard(Card[] table, Deck deck, int index) {
        table[index] = deck.takeRandomCard();
    }

    private void printTable(Card[] table) {

        System.out.print("Board: ");

        for (Card c : table) {
            if (c == null) {
                System.out.print("[--] ");
            } else {
                System.out.print(c.toLetterString() + " ");
            }
        }

        System.out.println();
    }

    @Test
    public void testSinglePlayerHas100PercentEquity() throws Exception  {
        Evaluator.getInstance();

        Deck deck = new Deck();

        List<HandInfo> players = new ArrayList<>();
        players.add(new HandInfo(
                0,
                new Card[]{
                        deck.takeRandomCard(),
                        deck.takeRandomCard()
                }));

        Map<Integer, Double> equity =
                EquityCalculator.calculateEquity(players, new Card[5], deck);

        assertEquals(1.0, equity.get(0), 1e-9);
    }
    @Test
    public void testAllPlayersReceiveEquity() throws Exception  {
        Evaluator.getInstance();

        Deck deck = new Deck();

        List<HandInfo> players = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            players.add(new HandInfo(
                    i,
                    new Card[]{
                            deck.takeRandomCard(),
                            deck.takeRandomCard()
                    }));
        }

        Map<Integer, Double> equity =
                EquityCalculator.calculateEquity(players, new Card[5], deck);

        assertEquals(players.size(), equity.size());

        for (HandInfo player : players) {
            assertTrue(equity.containsKey(player.playerID()));
        }
    }

        @Test
        public void testRoyalFlushOnBoardEveryoneSplitsPotEqually() throws Exception {

                Evaluator.getInstance();

                Deck deck = new Deck();

                List<HandInfo> players = new ArrayList<>();

                players.add(new HandInfo(0, new Card[] {
                        new Card(2, Suit.CLUBS),
                        new Card(7, Suit.DIAMONDS)
                }));

                players.add(new HandInfo(1, new Card[] {
                        new Card(9, Suit.CLUBS),
                        new Card(9, Suit.DIAMONDS)
                }));

                players.add(new HandInfo(2, new Card[] {
                        new Card(2, Suit.SPADES),
                        new Card(3, Suit.CLUBS)
                }));

                Card[] board = {
                        new Card(14, Suit.HEARTS), // A♥
                        new Card(13, Suit.HEARTS), // K♥
                        new Card(12, Suit.HEARTS), // Q♥
                        new Card(11, Suit.HEARTS), // J♥
                        new Card(10, Suit.HEARTS)  // T♥
                };

                Map<Integer, Double> equity =
                        EquityCalculator.calculateEquity(players, board, deck);

                double expectedEquity = 1.0 / players.size();

                for (HandInfo player : players) {

                        double eq = equity.get(player.playerID());

                        System.out.printf(
                                "Player %d : %.2f%%%n",
                                player.playerID(),
                                eq * 100);

                        assertEquals(
                                expectedEquity,
                                eq,
                                1e-9,
                                "Player " + player.playerID()
                                        + " should receive an equal share of the pot.");
                }
        }
        
}