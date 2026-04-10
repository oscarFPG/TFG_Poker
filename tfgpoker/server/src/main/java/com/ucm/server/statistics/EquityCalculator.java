package com.ucm.server.statistics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ucm.common.gameobjects.Card;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.gameobjects.Deck;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;

public class EquityCalculator {

    private static final int MONTE_CARLO_SIMULATIONS = 30000;

    public static Map<Integer, Double> calculateEquity(
            List<HandInfo> players,
            Card[] tableCards,
            Deck deck
    ) {

        int missing = countMissingCards(tableCards);

        if (missing == 0) {
            return calculateRiver(players, tableCards);
        }
        else if (missing == 1) {
            return calculateTurn(players, tableCards, deck);
        }
        else if (missing == 2) {
            return calculateFlop(players, tableCards, deck);
        }
        else {
            return calculateMonteCarlo(players, tableCards, deck);
        }
    }

   //RIVER (FINAL - 0 CARDS LEFT)
    private static Map<Integer, Double> calculateRiver(
            List<HandInfo> players,
            Card[] tableCards
    ) {

        Map<Integer, Double> result = initResult(players);

        List<PlayerEvaluation> evals =
                Evaluator.evaluateAllHands(players, tableCards);

        List<Integer> winners = getWinners(evals);

        for (int id : winners) {
            result.put(id, 1.0 / winners.size());
        }

        return result;
    }

   //TURN (1 CARD LEFT)
    private static Map<Integer, Double> calculateTurn(
            List<HandInfo> players,
            Card[] tableCards,
            Deck deck
    ) {

        Map<Integer, Integer> wins = initWins(players);
        List<Card> available = deck.getAvailableCards();

        for (Card river : available) {

            Card[] board = completeBoard(tableCards, river);

            List<PlayerEvaluation> evals =
                    Evaluator.evaluateAllHands(players, board);

            List<Integer> winners = getWinners(evals);

            for (int id : winners) {
                wins.put(id, wins.get(id) + 1);
            }
        }

        return normalize(wins);
    }

   //FLOP (2 CARDS LEFT)
    private static Map<Integer, Double> calculateFlop(
            List<HandInfo> players,
            Card[] tableCards,
            Deck deck
    ) {

        Map<Integer, Integer> wins = initWins(players);
        List<Card> available = deck.getAvailableCards();

        int n = available.size();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                Card turn = available.get(i);
                Card river = available.get(j);

                Card[] board = completeBoard(tableCards, turn, river);

                List<PlayerEvaluation> evals =
                        Evaluator.evaluateAllHands(players, board);

                List<Integer> winners = getWinners(evals);

                for (int id : winners) {
                    wins.put(id, wins.get(id) + 1);
                }
            }
        }

        return normalize(wins);
    }

   // PREFLOP (MONTE CARLO METHOD - 0 AND 3 CARD LEFT)
    private static Map<Integer, Double> calculateMonteCarlo(
            List<HandInfo> players,
            Card[] tableCards,
            Deck deck
    ) {

        Map<Integer, Integer> wins = initWins(players);
        List<Card> available = deck.getAvailableCards();
        Random rnd = new Random();

        int missing = countMissingCards(tableCards);

        for (int sim = 0; sim < MONTE_CARLO_SIMULATIONS; sim++) {

            Collections.shuffle(available, rnd);

            Card[] board = fillRandomBoard(tableCards, available, missing);

            List<PlayerEvaluation> evals =
                    Evaluator.evaluateAllHands(players, board);

            List<Integer> winners = getWinners(evals);

            for (int id : winners) {
                wins.put(id, wins.get(id) + 1);
            }
        }

        return normalize(wins);
    }


    private static int countMissingCards(Card[] tableCards) {
        int count = 0;
        for (Card c : tableCards) {
            if (c == null) count++;
        }
        return count;
    }

    private static Map<Integer, Double> initResult(List<HandInfo> players) {
        Map<Integer, Double> map = new HashMap<>();
        for (HandInfo h : players) {
            map.put(h.playerID(), 0.0);
        }
        return map;
    }

    private static Map<Integer, Integer> initWins(List<HandInfo> players) {
        Map<Integer, Integer> map = new HashMap<>();
        for (HandInfo h : players) {
            map.put(h.playerID(), 0);
        }
        return map;
    }

    private static Map<Integer, Double> normalize(Map<Integer, Integer> wins) {

        Map<Integer, Double> result = new HashMap<>();

        int total = wins.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<Integer, Integer> e : wins.entrySet()) {
            result.put(e.getKey(), e.getValue() / (double) total);
        }

        return result;
    }

   private static List<Integer> getWinners(List<PlayerEvaluation> evals) {

    int best = evals.stream()
            .mapToInt(e -> e.playerRank()) 
            .min()
            .orElse(Integer.MAX_VALUE);

    List<Integer> winners = new ArrayList<>();

    for (PlayerEvaluation e : evals) {
        if (e.playerRank() == best) {
            winners.add(e.playerID());
        }
    }

    return winners;
}

    private static Card[] completeBoard(Card[] tableCards, Card... extra) {

        Card[] board = new Card[5];
        int idx = 0;

        for (Card c : tableCards) {
            if (c != null) board[idx++] = c;
        }

        for (Card c : extra) {
            board[idx++] = c;
        }

        return board;
    }

    private static Card[] fillRandomBoard(
            Card[] tableCards,
            List<Card> available,
            int missing
    ) {

        Card[] board = new Card[5];
        int idx = 0;

        for (Card c : tableCards) {
            if (c != null) board[idx++] = c;
        }

        for (int i = 0; i < missing; i++) {
            board[idx++] = available.get(i);
        }

        return board;
    }
}