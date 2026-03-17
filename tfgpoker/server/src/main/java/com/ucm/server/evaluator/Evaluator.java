package com.ucm.server.evaluator;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import com.ucm.server.ServerMain;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.gameobjects.Suit;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.HandInfo;

/**
 * Utility class responsible for evaluating poker hands.
 * <p>
 * This implementation is based on a fast 5-card poker hand evaluation
 * algorithm using precomputed lookup tables.
 * <p>
 * Adapted from:
 * Cactus Kev's Poker Hand Evaluator
 * (http://suffe.cool/poker/evaluator.html)
 */
public class Evaluator {

    public enum RANK {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH
    }

    private static final  int PRIME_NUMBERS[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };

    private static short _flushes[];
    private static short _unique5[];
    private static short _hashAdjust[];
    private static short _hashValues[];

    private static Evaluator instance;


    private Evaluator() throws IOException {
        loadEvaluator();
    }

    public static Evaluator getInstance() throws IOException {

        if (instance == null) {
            instance = new Evaluator();
        }

        return instance;
    }


    private static List<String> getFileResource(String fileName) {

        String resourcePath = "/arrays/" + fileName;

        InputStream is = ServerMain.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new RuntimeException("Archivo no encontrado: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo archivo: " + resourcePath, e);
        }
    }

    private void loadEvaluator() throws IOException {

        List<String> lineasFlushes = getFileResource("flushes.txt");
        List<String> lineasUnique = getFileResource("unique.txt");
        List<String> lineasHashAdjust = getFileResource("hash_adjust.txt");
        List<String> lineasHashValues = getFileResource("hash_values.txt");

        _flushes = new short[lineasFlushes.size()];
        for (int i = 0; i < lineasFlushes.size(); i++) {
            _flushes[i] = Short.parseShort(lineasFlushes.get(i).trim());
        }

        _unique5 = new short[lineasUnique.size()];
        for (int i = 0; i < lineasUnique.size(); i++) {
            _unique5[i] = Short.parseShort(lineasUnique.get(i).trim());
        }

        _hashAdjust = new short[lineasHashAdjust.size()];
        for (int i = 0; i < lineasHashAdjust.size(); i++) {
            _hashAdjust[i] = Short.parseShort(lineasHashAdjust.get(i).trim());
        }

        _hashValues = new short[lineasHashValues.size()];
        for (int i = 0; i < lineasHashValues.size(); i++) {
            _hashValues[i] = Short.parseShort(lineasHashValues.get(i).trim());
        }
    }

    public static List<IPokerPlayer> evaluateAllHands(HandInfo[] playerHands, Card[] tableCards) {

        int encodedPlayerCards[][] = new int[playerHands.length][2];
        int encodedTableCards[] = new int[tableCards.length];
        short bestHandValue[] = new short[playerHands.length];
        int encoded7Cards[] = new int[7];


        for (int i = 0; i < playerHands.length; i++) {
            encodedPlayerCards[i][0] = encodeCard(playerHands[i].cards()[0]);
            encodedPlayerCards[i][1] = encodeCard(playerHands[i].cards()[1]);
        }
        for (int i = 0; i < tableCards.length; i++) {
            encodedTableCards[i] = encodeCard(tableCards[i]);
        }

        // For each player, calculate the best 5 cards hand between all 7 cards(player cards + all table cards)
        short tableRank = evaluate5hand(
                encodedTableCards[0],
                encodedTableCards[1],
                encodedTableCards[2],
                encodedTableCards[3],
                encodedTableCards[4]
        );
        short bestValue = Short.MAX_VALUE;
        short value = 0;
        for (int i = 0; i < playerHands.length; i++) {

            encoded7Cards[0] = encodedPlayerCards[i][0];    // First player card
            encoded7Cards[1] = encodedPlayerCards[i][1];    // Second player card
            encoded7Cards[2] = encodedTableCards[0];        // First card on the table
            encoded7Cards[3] = encodedTableCards[1];        // Second card on the table
            encoded7Cards[4] = encodedTableCards[2];        // Third card on the table
            encoded7Cards[5] = encodedTableCards[3];        // Fourth card on the table
            encoded7Cards[6] = encodedTableCards[4];        // Fifth card on the table

            // Assign best hand value obtained between:
            // One or both player cards + 3 on the table
            // All 5 on the table
            value = (short) Math.min(tableRank, evaluate7hand(encoded7Cards));
            bestHandValue[i] = value;
            bestValue = (short) Math.min(value, bestValue);
        }

        // Select all players with the best value hand
        List<IPokerPlayer> winners = new ArrayList<>();
        for (int i = 0; i < playerHands.length; i++) {
            if (bestHandValue[i] == bestValue)
                winners.add(playerHands[i].player());
        }

        return winners;
    }

    public static short evaluate5hand(final int card1, final int card2, final int card3, final int card4, final int card5) {

        int q = (card1 | card2 | card3 | card4 | card5) >>> 16;
        boolean bIsFlush = (card1 & card2 & card3 & card4 & card5 & 0xF000) != 0;
        short s = _unique5[q];

        // This checks for Flushes and Straight Flushes
        if (bIsFlush)
            return _flushes[q];

        // This checks for Straights and High Card hands
        if (s != 0)
            return s;

        // This performs a perfect-hash lookup for remaining hands.
        q = (card1 & 0xFF) * (card2 & 0xFF) * (card3 & 0xFF) * (card4 & 0xFF) *
                (card5 & 0xFF);
        return _hashValues[findFast(q)];
    }

    /**
     * This method calculates the best hand making all the combinations ONLY
     * including at least one of players card
     * Both cards must be the first two on the array
     * 
     * @param cards
     * @return
     */
    private static short evaluate7hand(final int[] cards) {

        if (cards.length != 7) // TODO : Lanzar excepcion
            return -1;

        short bestHandValue = Short.MAX_VALUE;
        short value = 0;
        // Includes both player cards + 3 on the table
        for (int first = 2; first < cards.length - 2; first++) {
            for (int second = first + 1; second < cards.length - 1; second++) {
                for (int third = second + 1; third < cards.length; third++) {
                    value = evaluate5hand(cards[0], cards[1], cards[first], cards[second], cards[third]);
                    bestHandValue = (short) Math.min(bestHandValue, value);
                }
            }
        }

        // Includes only one player card
        for (int first = 2; first < cards.length - 3; first++) {
            for (int second = first + 1; second < cards.length - 2; second++) {
                for (int third = second + 1; third < cards.length - 1; third++) {
                    for (int fourth = third + 1; fourth < cards.length; fourth++) {

                        // Using first card + 4 on the table
                        value = evaluate5hand(cards[0], cards[first], cards[second], cards[third], cards[fourth]);
                        bestHandValue = (short) Math.min(bestHandValue, value);

                        // Using second card + 4 on the table
                        value = evaluate5hand(cards[1], cards[first], cards[second], cards[third], cards[fourth]);
                        bestHandValue = (short) Math.min(bestHandValue, value);
                    }
                }
            }
        }

        return bestHandValue;
    }

    private static int findFast(int u) {

        int a, b, r;

        u += 0xE91AAA35;
        u ^= u >>> 16;
        u += u << 8;
        u ^= u >>> 4;
        b = ((u >>> 8) & 0x1FF);
        a = ((u + (u << 2)) >>> 19);
        r = (a ^ _hashAdjust[b]);

        return r;
    }

    private static RANK handRank(short val) {

        if (val > 6185)
            return RANK.HIGH_CARD; // 1277 high card
        else if (val > 3325)
            return RANK.ONE_PAIR; // 2860 one pair
        else if (val > 2467)
            return RANK.TWO_PAIR; // 858 two pair
        else if (val > 1609)
            return RANK.THREE_OF_A_KIND; // 858 three-kind
        else if (val > 1599)
            return RANK.STRAIGHT; // 10 straights
        else if (val > 322)
            return RANK.FLUSH; // 1277 flushes
        else if (val > 166)
            return RANK.FULL_HOUSE; // 156 full house
        else if (val > 10)
            return RANK.FOUR_OF_A_KIND; // 156 four-kind
        else
            return RANK.STRAIGHT_FLUSH; // 10 straight-flushes
    }

    public static int encodeCard(Card c) {

        int prime = Evaluator.PRIME_NUMBERS[c.getNumber() - 2];
        int rank = c.getNumber() - 2;
        int suit = encodeSuit(c.getSuit());
        int bitmask = 1 << rank;

        return prime | (rank << 8) | (suit << 12) | (bitmask << 16);
    }

    private static int encodeSuit(Suit suit) {

        switch (suit) {
            case Suit.SPADES:
                return 1;

            case Suit.HEARTS:
                return 2;

            case Suit.DIAMONDS:
                return 4;

            default:
                return 8;
        }
    }

}