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

/**
 * Utility class responsible for calculating equity (win probability)
 * of players in a Texas Hold'em game.
 * 
 * <p>
 * The calculation method depends on the current game street:
 * <ul>
 *   <li>River → exact evaluation</li>
 *   <li>Turn → iterate all possible rivers</li>
 *   <li>Flop → iterate all possible turn + river combinations</li>
 *   <li>Preflop / partial → Monte Carlo simulation</li>
 * </ul>
 * 
 * <p>
 * Equity is returned as a probability between 0.0 and 1.0 for each player.
 */

public class EquityCalculator {

     /** Number of simulations used in Monte Carlo approximation */
    private static final int MONTE_CARLO_SIMULATIONS = 30000;

    /**
     * Calculates equity for all players given current board state.
     *
     * @param players list of players with their hole cards
     * @param tableCards community cards (can contain nulls)
     * @param deck remaining deck
     * @return map of playerID → equity (0.0 - 1.0)
     */
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

    /**
     * Calculates exact equity on river (no unknown cards).
     * 
     * @param players players in the hand
     * @param tableCards full board (5 cards)
     * @return equity distribution
     */
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

   /**
     * Calculates equity on turn by iterating all possible river cards.
     *
     * @param players players
     * @param tableCards board with 4 known cards
     * @param deck remaining deck
     * @return normalized equity
     */
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

   /**
     * Calculates equity on flop by iterating all possible turn and river combinations.
     *
     * @param players players
     * @param tableCards board with 3 known cards
     * @param deck remaining deck
     * @return normalized equity
     */
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

    /**
     * Calculates equity using Monte Carlo simulation.
     * Used for preflop or incomplete boards with many unknown cards.
     *
     * @param players players
     * @param tableCards partial board
     * @param deck remaining deck
     * @return approximated equity
     */
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


    /**
     * Counts missing (null) cards in the board.
     */
    private static int countMissingCards(Card[] tableCards) {
        int count = 0;
        for (Card c : tableCards) {
            if (c == null) count++;
        }
        return count;
    }

     /**
     * Initializes equity result map with 0.0 values.
     */
    private static Map<Integer, Double> initResult(List<HandInfo> players) {
        Map<Integer, Double> map = new HashMap<>();
        for (HandInfo h : players) {
            map.put(h.playerID(), 0.0);
        }
        return map;
    }


     /**
     * Initializes win counter map.
     */
    private static Map<Integer, Integer> initWins(List<HandInfo> players) {
        Map<Integer, Integer> map = new HashMap<>();
        for (HandInfo h : players) {
            map.put(h.playerID(), 0);
        }
        return map;
    }


    /**
     * Converts win counts into normalized probabilities.
     *
     * @param wins map of playerID → number of wins
     * @return map of playerID → equity (0.0 - 1.0)
     */
    private static Map<Integer, Double> normalize(Map<Integer, Integer> wins) {

        Map<Integer, Double> result = new HashMap<>();

        int total = wins.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<Integer, Integer> e : wins.entrySet()) {
            result.put(e.getKey(), e.getValue() / (double) total);
        }

        return result;
    }


    /**
     * Returns the list of winning players (handles ties).
     */
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


     /**
     * Completes board with given extra cards.
     */
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


     /**
     * Fills missing board cards randomly from available deck.
     */
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