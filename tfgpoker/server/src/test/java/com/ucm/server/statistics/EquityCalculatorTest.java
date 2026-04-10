package com.ucm.server.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ucm.common.gameobjects.Card;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.gameobjects.Deck;
import com.ucm.server.middleclasses.HandInfo;

public class EquityCalculatorTest {

    private static final int ITERATIONS = 5; 
    public static void main(String[] args) throws Exception {

        Evaluator.getInstance();

        
        System.out.println("   EQUITY CALCULATOR TEST");
        System.out.println("---------------------------------\n");

        for (int i = 1; i <= ITERATIONS; i++) {
           
            System.out.println("ESCENARIO " + i);
            System.out.println("---------------------------------\n");

            runFullGameSimulation(2 + new Random().nextInt(8)); //2 a 8 jugadores
        }
    }


    private static void runFullGameSimulation(int numPlayers) {

        Deck deck = new Deck();
        List<HandInfo> players = new ArrayList<>();
        System.out.println("\n--- PLAYERS ---");

        for (int i = 0; i < numPlayers; i++) {
            Card c1 = deck.takeRandomCard();
            Card c2 = deck.takeRandomCard();

            players.add(new HandInfo(i, new Card[]{c1, c2}));

            System.out.println("Player " + i + ": [" + c1 + " " + c2 + "]");
        }

        Card[] table = new Card[5];

    

        System.out.println("\n-------PREFLOP -------");
        printEquity(players, table, deck);

    
        dealCard(table, deck, 0);
        dealCard(table, deck, 1);
        dealCard(table, deck, 2);

        System.out.println("\n-------- FLOP --------");
        printTable(table);
        printEquity(players, table, deck);

       
        dealCard(table, deck, 3);

        System.out.println("\n----- TURN -------");
        printTable(table);
        printEquity(players, table, deck);

      
        dealCard(table, deck, 4);

        System.out.println("\n------ RIVER ------");
        printTable(table);
        printEquity(players, table, deck);
    }

   
    private static void dealCard(Card[] table, Deck deck, int index) {
        table[index] = deck.takeRandomCard();
    }

    
    private static void printEquity(
            List<HandInfo> players,
            Card[] table,
            Deck deck
    ) {

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

       
        if (Math.abs(total - 1.0) > 0.01) {
            System.out.println("WARNING: Equity no suma 1!");
        }
    }

    
    private static void printTable(Card[] table) {

        System.out.print("Board: ");

        for (Card c : table) {
            if (c == null) System.out.print("[--] ");
            else System.out.print(c + " ");
        }

        System.out.println();
    }
}