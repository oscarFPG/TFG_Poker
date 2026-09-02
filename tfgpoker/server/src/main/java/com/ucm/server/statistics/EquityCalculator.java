package com.ucm.server.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Deck;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;

/**
 * Utility class responsible for estimating the equity (win probability) of
 * players in a Texas Hold'em hand.
 *
 * <p>
 * Equity is estimated using Monte Carlo simulation. Each player is evaluated
 * independently as the hero, while the opponents' hole cards are randomly
 * generated for every simulation from the remaining unseen cards.
 * </p>
 */
public class EquityCalculator {

    /** Number of Monte Carlo simulations performed for each player. */
    private static final int MONTE_CARLO_SIMULATIONS = 30000;

    /** Random number generator used for shuffling cards. */
    private static final Random RANDOM = new Random();

    /**
     * Calculates the equity of every active player.
     *
     * @param players    active players with their hole cards
     * @param tableCards current community cards (missing cards are {@code null})
     * @param deck       game deck
     * @return map containing the estimated equity of each player
     */
    public static Map<Integer, Double> calculateEquity(
            List<HandInfo> players,
            Card[] tableCards,
            Deck deck) {

        Map<Integer, Double> equity = new HashMap<>();

        if (players.size() <= 1) {
            for (HandInfo player : players) {
                equity.put(player.playerID(), 1.0);
            }
            return equity;
        }

        for (HandInfo hero : players) {
            equity.put(
                    hero.playerID(),
                    calculateMonteCarlo(hero, players, tableCards, deck)
            );
        }

        return equity;
    }

    /**
     * Estimates the equity of a single player using Monte Carlo simulation.
     *
     * @param hero       player whose equity is being estimated
     * @param players    active players
     * @param tableCards current community cards
     * @param deck       game deck
     * @return estimated equity of the hero
     */
    private static double calculateMonteCarlo(
            HandInfo hero,
            List<HandInfo> players,
            Card[] tableCards,
            Deck deck) {

        double heroWins = 0.0;
        int missing = countMissingCards(tableCards);

        final List<Card> baseDeck = deck.getAllCards();


        Evaluator evaluator;

        try {
            evaluator = Evaluator.getInstance();
        } catch (EvaluatorException e) {
            throw new RuntimeException(e);
        }

        setUnavailable(baseDeck, hero.cards()[0]);
        setUnavailable(baseDeck, hero.cards()[1]);


        for (Card card : tableCards) {
            if (card != null) {
                setUnavailable(baseDeck, card);
            }
        }

        for (int sim = 0; sim < MONTE_CARLO_SIMULATIONS; sim++) {

            List<Card> available = new ArrayList<>();

            for (Card card : baseDeck) {

                if (card.getAvailable()) {
                    available.add(card);
                }
            }

            Collections.shuffle(available, RANDOM);


            List<HandInfo> simulationPlayers =
                    buildSimulationPlayers(hero, players, available);

            int offset = (players.size() - 1) * 2;

            Card[] board = fillRandomBoard(
                    tableCards,
                    available.subList(offset, available.size()),
                    missing
            );

            List<PlayerEvaluation> evaluations =
                    evaluator.evaluateAllHands(simulationPlayers, board);

            List<Integer> winners = getWinners(evaluations);

            if (winners.contains(hero.playerID())) {
                heroWins += 1.0 / winners.size();
            }
        }

        return heroWins / MONTE_CARLO_SIMULATIONS;
    }

    /**
     * Counts the number of unknown community cards.
     *
     * @param tableCards current community cards
     * @return number of missing cards
     */
    private static int countMissingCards(Card[] tableCards) {

        int count = 0;

        for (Card card : tableCards) {
            if (card == null) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the identifiers of all players sharing the best hand.
     *
     * @param evaluations evaluated hands
     * @return list of winning player identifiers
     */


    /** Gets the players with the best hand. 
     *  @param evaluations evaluated hands  
     * @return identifiers of the winning players
    */
    private static List<Integer> getWinners(List<PlayerEvaluation> evaluations) {

        int bestRank = evaluations.stream()
                .mapToInt(PlayerEvaluation::playerRank)
                .min()
                .orElse(Integer.MAX_VALUE);

        

        List<Integer> winners = new ArrayList<>();

        for (PlayerEvaluation evaluation : evaluations) {
            if (evaluation.playerRank() == bestRank) {
                winners.add(evaluation.playerID());
            }
        }

        return winners;
    }

    /**
     * Completes the board by filling the missing community cards with cards
     * taken from the available deck.
     *
     * @param tableCards current community cards
     * @param available  available cards
     * @param missing    number of missing cards
     * @return completed board
     */
    private static Card[] fillRandomBoard(
            Card[] tableCards,
            List<Card> available,
            int missing) {

        Card[] board = new Card[5];
        int index = 0;

        for (Card card : tableCards) {
            if (card != null) {
                board[index++] = card;
            }
        }

        for (int i = 0; i < missing; i++) {
            board[index++] = available.get(i);
        }

        return board;
    }

    /**
     * Builds the player list used in a simulation by keeping the hero's real
     * hole cards and assigning random hole cards to every opponent.
     *
     * @param hero      hero player
     * @param players   active players
     * @param available remaining available cards
     * @return simulated player list
     */
    private static List<HandInfo> buildSimulationPlayers(
            HandInfo hero,
            List<HandInfo> players,
            List<Card> available) {

        List<HandInfo> simulationPlayers = new ArrayList<>(players.size());

        simulationPlayers.add(hero);

        int position = 0;

        for (HandInfo player : players) {

            if (player.playerID() == hero.playerID()) {
                continue;
            }

            simulationPlayers.add(
                    new HandInfo(
                            player.playerID(),
                            new Card[]{
                                    available.get(position++),
                                    available.get(position++)
                            }
                    )
            );
        }

        return simulationPlayers;
    }


    /**
     * Marks the specified card as unavailable in the given deck.
     * <p>
     * The target card is identified by matching its rank and suit. Once found,
     * its availability is set to {@code false}. If the card is not present in the
     * deck, no changes are made.
     *
     * @param deck   list of cards where the target card will be searched
     * @param target card to mark as unavailable
     */
    private static void setUnavailable(List<Card> deck, Card target) {

        for (Card card : deck) {

            if (card.getNumber() == target.getNumber()
                    && card.getSuit() == target.getSuit()) {

                card.setAvailable(false);
                return;
            }
        }
    }
}