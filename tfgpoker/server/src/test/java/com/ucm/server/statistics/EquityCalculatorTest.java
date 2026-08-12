package com.ucm.server.statistics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Deck;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.middleclasses.HandInfo;

public class EquityCalculatorTest {

    private static final int ITERATIONS = 5;

    // ---------------------- TEST GENERAL ----------------------

    @Test
    public void testMultipleRandomScenarios() throws Exception {

        Evaluator.getInstance();

        for (int i = 0; i < ITERATIONS; i++) {

            int numPlayers = 2 + new Random().nextInt(7); // 2 a 8 jugadores

            System.out.println("\nESCENARIO " + (i + 1));
            runFullGameSimulation(numPlayers);
        }
    }

    // ---------------------- SIMULACIÓN COMPLETA ----------------------

    private void runFullGameSimulation(int numPlayers) throws CancelGameException {

        Deck deck = new Deck();
        List<HandInfo> players = new ArrayList<>();

        System.out.println("\n--- PLAYERS ---");

        for (int i = 0; i < numPlayers; i++) {
            Card c1 = deck.takeRandomCard();
            Card c2 = deck.takeRandomCard();

            players.add(new HandInfo(i, new Card[]{c1, c2}));

            System.out.println("Player " + i + ": [" + c1.toLetterString() + " " + c2.toLetterString() + "]");
        }

        Card[] table = new Card[5];

        // PREFLOP
        System.out.println("\n--- PREFLOP ---");
        assertEquity(players, table, deck);

        // FLOP
        dealCard(table, deck, 0);
        dealCard(table, deck, 1);
        dealCard(table, deck, 2);

        System.out.println("\n--- FLOP ---");
        printTable(table);
        assertEquity(players, table, deck);

        // TURN
        dealCard(table, deck, 3);

        System.out.println("\n--- TURN ---");
        printTable(table);
        assertEquity(players, table, deck);

        // RIVER
        dealCard(table, deck, 4);

        System.out.println("\n--- RIVER ---");
        printTable(table);
        assertEquity(players, table, deck);
    }

    // ---------------------- ASSERT EQUITY ----------------------

    private void assertEquity(
            List<HandInfo> players,
            Card[] table,
            Deck deck
    ) throws CancelGameException {

        Map<Integer, Double> equity =
                EquityCalculator.calculateEquity(players, table, deck);

        double total = 0;

        for (Map.Entry<Integer, Double> e : equity.entrySet()) {

            double eq = e.getValue();
            total += eq;

            System.out.printf("Player %d : %.2f%%\n",
                    e.getKey(), eq * 100);
        }

        System.out.printf("TOTAL: %.4f\n", total);

        //ASSERT CLAVE
        assertTrue(Math.abs(total - 1.0) <= 0.01,
                "Equity total no suma 1. Valor: " + total);
    }

    // ---------------------- HELPERS ----------------------

    private void dealCard(Card[] table, Deck deck, int index) {
        table[index] = deck.takeRandomCard();
    }

    private void printTable(Card[] table) {

        System.out.print("Board: ");

        for (Card c : table) {
            if (c == null) System.out.print("[--] ");
            else System.out.print(c.toLetterString() + " ");
        }

        System.out.println();
    }
}