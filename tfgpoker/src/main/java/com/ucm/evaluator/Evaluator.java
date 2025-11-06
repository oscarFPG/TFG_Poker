package com.ucm.evaluator;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;

import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Suit;
import com.ucm.middleclasses.HandInfo;

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

    private static int PRIME_NUMBERS[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };

    private static short _flushes[];
    private static short _unique5[];
    private static short _hashAdjust[];
    private static short _hashValues[];

    private static Evaluator instance;

    /**
     * Evitar instanciamiento desde fuera de esta clase
     * 
     * @throws IOException
     */
    private Evaluator() throws IOException {
        loadEvaluator();
    }

    public static Evaluator getInstance() throws IOException {

        if (instance == null) {
            instance = new Evaluator();
        }

        return instance;
    }

    private void loadEvaluator() throws IOException {

        List<String> lineasFlushes = Files.readAllLines(Path.of("../arrays/flushes.txt"));
        List<String> lineasUnique = Files.readAllLines(Path.of("../arrays/unique.txt"));
        List<String> lineasHashAdjust = Files.readAllLines(Path.of("../arrays/hash_adjust.txt"));
        List<String> lineasHashValues = Files.readAllLines(Path.of("../arrays/hash_values.txt"));

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

    public static Player evaluateAllHands(HandInfo[] playerHands, Card[] tableCards) {

        int encodedPlayerCards[][] = new int[playerHands.length][2];
        int encodedTableCards[] = new int[tableCards.length];
        Player winner = null;

        for (int i = 0; i < playerHands.length; i++) {
            encodedPlayerCards[i][0] = encodeCard(playerHands[i].cards()[0]);
            encodedPlayerCards[i][1] = encodeCard(playerHands[i].cards()[1]);
        }
        for (int i = 0; i < tableCards.length; i++) {
            encodedTableCards[i] = encodeCard(tableCards[i]);
        }

        Card[] test = {
                new Card(2, Suit.CLUBS),
                new Card(3, Suit.CLUBS),
                new Card(4, Suit.CLUBS),
                new Card(5, Suit.CLUBS),
                new Card(6, Suit.CLUBS)
        };

        int[] testEncode = {
                encodeCard(test[0]),
                encodeCard(test[1]),
                encodeCard(test[2]),
                encodeCard(test[3]),
                encodeCard(test[4])
        };

        short value = evaluate5hand(testEncode);
        RANK rank = hand_rank(value);

        return winner;
    }

    private static short evaluate5hand(int[] cards) {

        if (cards.length != 5)
            return -1;

        int q = (cards[0] | cards[1] | cards[2] | cards[3] | cards[4]) >> 16;
        boolean bIsFlush = (cards[0] & cards[1] & cards[2] & cards[3] & cards[4] & 0xf000) != 0;
        short s = _unique5[q];

        // This checks for Flushes and Straight Flushes.
        if (bIsFlush)
            return _flushes[q];

        // This checks for Straights and High Card hands.
        if (s != 0)
            return s;

        // This performs a perfect-hash lookup for remaining hands.
        q = (cards[0] & 0xff) * (cards[1] & 0xff) * (cards[2] & 0xff) * (cards[3] & 0xff) * (cards[4] & 0xff);
        return _hashValues[findFast(q)];
    }

    private static short findFast(int u) {

        short a, b, r;

        u += 0xe91aaa35;
        u ^= u >> 16;
        u += u << 8;
        u ^= u >> 4;
        b = (byte) ((u >> 8) & 0x1ff);
        a = (byte) ((u + (u << 2)) >> 19);
        r = (byte) (a ^ _hashAdjust[b]);

        return r;
    }

    private static RANK hand_rank(short val) {

        if (val > 6185)
            return RANK.HIGH_CARD;          // 1277 high card
        if (val > 3325)
            return RANK.ONE_PAIR;           // 2860 one pair
        if (val > 2467)
            return RANK.TWO_PAIR;           // 858 two pair
        if (val > 1609)
            return RANK.THREE_OF_A_KIND;    // 858 three-kind
        if (val > 1599)
            return RANK.STRAIGHT;           // 10 straights
        if (val > 322)
            return RANK.FLUSH;              // 1277 flushes
        if (val > 166)
            return RANK.FULL_HOUSE;         // 156 full house
        if (val > 10)
            return RANK.FOUR_OF_A_KIND;     // 156 four-kind
        return RANK.STRAIGHT_FLUSH;         // 10 straight-flushes
    }

    private static int encodeCard(Card c) {

        int card = (byte) Evaluator.PRIME_NUMBERS[c.getNumber() - 2];

        card |= ((byte) c.getNumber() << 8);
        card |= ((byte) encodeSuit(c.getSuit()) << 12);
        card |= ((byte) encodeRank(c.getNumber()) << 16);

        return card;
    }

    private static byte encodeSuit(Suit suit) {

        byte s = 0;
        switch (suit) {
            case Suit.SPADES:
                s |= (1 << 0);
                break;

            case Suit.HEARTS:
                s |= (1 << 1);
                break;

            case Suit.DIAMONDS:
                s |= (1 << 2);
                break;

            default:
                s |= (1 << 3);
                break;
        }

        return s;
    }

    private static byte encodeRank(int rank) {
        return (byte) (1 << (rank - 2));
    }

}