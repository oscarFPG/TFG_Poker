package com.ucm.server.evaluator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.ServerMain;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;


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

    /**
     * Enumeration representing the different ranks of poker hands.
     * The ranks are ordered from lowest to highest.
     */
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

    /**
     * Array of prime numbers used for encoding card values in the hand evaluation algorithm.
     * Each prime number corresponds to a card rank, starting from 2 (for rank 2) to 14 (for rank Ace).
     * The prime numbers are used to create a unique product for each combination of card ranks, allowing for efficient hand evaluation.
     */
    private static final  int PRIME_NUMBERS[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };

    /**
     * Lookup tables used for evaluating poker hands.
     * Is used to determine the rank of flush hands, while the {@link #_unique5} array is used for unique 5-card combinations.
     * These tables are used in conjunction with the {@link #evaluate5hand(int, int, int, int, int)} and {@link #evaluate7hand(int[])} methods to efficiently determine the best hand value for a given set of cards.
     */
    private static short _flushes[];

    /**
     * Lookup tables used for evaluating poker hands.
     * The {@link #_hashAdjust} array is used for adjusting the hash values of card combinations, while the {@link #_hashValues} array is used to store the final hand values for different card combinations.
     * These tables are used in conjunction with the {@link #evaluate5hand(int, int, int, int, int)} and {@link #evaluate7hand(int[])} methods to efficiently determine the best hand value for a given set of cards.
     */
    private static short _unique5[];

    /**
     * Lookup tables used for evaluating poker hands.
     * The {@link #_hashAdjust} array is used for adjusting the hash values of card combinations, while the {@link #_hashValues} array is used to store the final hand values for different card combinations.
     * These tables are used in conjunction with the {@link #evaluate5hand(int, int, int, int, int)} and {@link #evaluate7hand(int[])} methods to efficiently determine the best hand value for a given set of cards.
     */
    private static short _hashAdjust[];

    /**
     * Lookup tables used for evaluating poker hands.
     * The {@link #_hashAdjust} array is used for adjusting the hash values of card combinations, while the {@link #_hashValues} array is used to store the final hand values for different card combinations.
     * These tables are used in conjunction with the {@link #evaluate5hand(int, int, int, int, int)} and {@link #evaluate7hand(int[])} methods to efficiently determine the best hand value for a given set of cards.
     */
    private static short _hashValues[];

    /**
     * Singleton instance of the Evaluator class.
     * This instance is used to ensure that only one instance of the Evaluator class is created and shared across the application, allowing for efficient hand evaluation without the need to repeatedly load the lookup tables.
     * The instance is initialized when the {@link #getInstance()} method is called for the first time, and subsequent calls to this method will return the same instance.
     * @see #getInstance()
     */
    private static Evaluator instance;



    /**
     * Private constructor for the Evaluator class.
     * This constructor is responsible for loading the lookup tables used for hand evaluation.
     * @throws EvaluatorException if there is an error loading the lookup tables, such as missing or malformed files.
     */
    private Evaluator() throws EvaluatorException {
        loadEvaluator();
    }

    /**
     * Returns the singleton instance of the Evaluator class.
     * If the instance has not been created yet, it will be initialized by calling the private constructor, which loads the lookup tables used for hand evaluation.
     * @see #Evaluator()
     * @return the singleton instance of the Evaluator class
     * @throws EvaluatorException if there is an error loading the lookup tables, such as missing or malformed files.
     */
    public static Evaluator getInstance() throws EvaluatorException {

        if (instance == null) {
            instance = new Evaluator();
        }

        return instance;
    }


    /**
     * Reads a resource file from the classpath and returns its contents as a list of strings, where each string represents a line in the file.
     * The resource file is expected to be located in the "/arrays/" directory within the classpath.
     * @param fileName the name of the resource file to read (e.g., "flushes.txt", "unique.txt", etc.)
     * @return a list of strings representing the lines in the resource file
     */
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

    /**
     * Loads the lookup tables used for evaluating poker hands from resource files.
     * The method reads the contents of the "flushes.txt", "unique.txt", "hash_adjust.txt", and "hash_values.txt" files, and populates the corresponding arrays used for hand evaluation.
     * @throws EvaluatorException if there is an error loading the lookup tables, such as missing or malformed files.
     */
    private void loadEvaluator() throws EvaluatorException {

        try {

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
        catch(Exception e) {
            throw new EvaluatorException( String.format("Error loading the evaluator: %s", e.getMessage()) );
        }
    }

    /**
     * Evaluates the best poker hand for each player based on their hole cards and the community cards on the table.
     * The method calculates the best 5-card hand for each player by considering all possible combinations of their hole cards and the community cards, and returns a list of PlayerEvaluation objects containing the player ID and their best hand value.
     * @param playerHands a list of HandInfo objects representing the hole cards of each player, where each HandInfo object contains the player ID and their two hole cards
     * @param tableCards an array of Card objects representing the community cards on the table (flop, turn, and river)
     * @return a list of {@link PlayerEvaluation} objects, where each object contains the player ID and their best hand value based on the evaluated hands
     */
    public List<PlayerEvaluation> evaluateAllHands(List<HandInfo> playerHands, Card[] tableCards) {

        List<PlayerEvaluation> playersEval = new ArrayList<>( playerHands.size() );
        int encodedPlayerCards[][] = new int[ playerHands.size() ][2];
        int encodedTableCards[] = new int[ tableCards.length ];
        int encoded7Cards[] = new int[7];


        for (int i = 0; i < playerHands.size(); i++) {
            encodedPlayerCards[i][0] = encodeCard( playerHands.get(i).cards()[0] );
            encodedPlayerCards[i][1] = encodeCard( playerHands.get(i).cards()[1] );
        }
        for (int i = 0; i < tableCards.length; i++) {
            encodedTableCards[i] = encodeCard( tableCards[i] );
        }

        // For each player, calculate the best 5 cards hand between all 7 cards(player cards + all table cards)
        final short tableRank = evaluate5hand(
            encodedTableCards[0],
            encodedTableCards[1],
            encodedTableCards[2],
            encodedTableCards[3],
            encodedTableCards[4]
        );
        for (int i = 0; i < playerHands.size(); i++) {

            final int playerID = playerHands.get(i).playerID(); // Player to evaluate
            encoded7Cards[0] = encodedPlayerCards[i][0];    // First player card
            encoded7Cards[1] = encodedPlayerCards[i][1];    // Second player card
            encoded7Cards[2] = encodedTableCards[0];        // First card on the table
            encoded7Cards[3] = encodedTableCards[1];        // Second card on the table
            encoded7Cards[4] = encodedTableCards[2];        // Third card on the table
            encoded7Cards[5] = encodedTableCards[3];        // Fourth card on the table
            encoded7Cards[6] = encodedTableCards[4];        // Fifth card on the table

            // Assign best hand value obtained between:
            // One player card + 4 from the table
            // Both player cards + 3 from the table
            // All 5 cards from the table
            short value = (short) Math.min(tableRank, evaluate7hand(encoded7Cards));
            playersEval.add( new PlayerEvaluation(playerID, value) );
        }

        return playersEval;
    }

    /**
     * Evaluates the best 5-card poker hand from the given 5 encoded card values.
     * The method uses precomputed lookup tables to efficiently determine the rank of the hand based on the encoded card values, which include information about the card ranks and suits.
     * @param card1 the encoded value of the first card
     * @param card2 the encoded value of the second card
     * @param card3 the encoded value of the third card
     * @param card4 the encoded value of the fourth card
     * @param card5 the encoded value of the fifth card
     * @return a short value representing the rank of the best 5-card hand, where lower values indicate stronger hands (e.g., a straight flush has a lower value than a high card)
     */
    public short evaluate5hand(final int card1, final int card2, final int card3, final int card4, final int card5) {

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
     * @param cards the array of encoded card values
     * @return the best hand value among all possible 5-card combinations
     */
    private short evaluate7hand(final int[] cards) {

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

    /**
     * This method calculates a hash value for the given integer input using a series of bitwise operations and adjustments based on precomputed lookup tables.
     * The resulting hash value is used for efficient hand evaluation in the poker hand evaluator.
     * @param u the integer input for which to calculate the hash value
     * @return an integer representing the calculated hash value based on the input and the lookup tables
     */
    private int findFast(int u) {

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

    /**
     * Returns the name of the poker hand rank corresponding to the given hand value.
     * The method uses the {@link #handRank(short)} method to determine the rank of the hand based on the provided hand value, and then returns the name of the rank as a string.
     * @param val the hand value for which to determine the rank name
     * @return the name of the poker hand rank
     */
    public static String getRankName(short val) {

        RANK rank = handRank(val);
        return rank.name();
    }

    /**
     * Determines the rank of a poker hand based on the given hand value.
     * @param val the hand value for which to determine the rank
     * @return the {@link RANK} enumeration value representing the rank of the hand
     */
    public static RANK handRank(short val) {

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

    /**
     * Encodes a Card object into an integer representation used for hand evaluation.
     * The encoding includes information about the card's rank, suit, and a unique prime number associated with the card's rank, allowing for efficient hand evaluation using precomputed lookup tables.
     * @param c the {@link Card} object to encode, which contains the card's rank and suit
     * @return the encoded integer value representing the card
     */
    public static int encodeCard(Card c) {

        int prime = Evaluator.PRIME_NUMBERS[c.getNumber() - 2];
        int rank = c.getNumber() - 2;
        int suit = encodeSuit(c.getSuit());
        int bitmask = 1 << rank;

        return prime | (rank << 8) | (suit << 12) | (bitmask << 16);
    }

    /**
     * Encodes a Suit enumeration value into an integer representation used for hand evaluation.
     * The encoding assigns a unique integer value to each suit, allowing for efficient hand evaluation using precomputed lookup tables.
     * @param suit the {@link Suit} enumeration value to encode, which represents the suit of a card (e.g., SPADES, HEARTS, DIAMONDS, CLUBS)
     * @return the encoded integer value representing the suit
     */
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