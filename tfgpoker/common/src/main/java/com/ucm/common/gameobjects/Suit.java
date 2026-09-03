package com.ucm.common.gameobjects;

import com.ucm.common.GameType;

/**
 * Represents the four suits of a standard deck of playing cards.
 * <p>
 * Each suit is associated with:
 * </p>
 * <ul>
 *   <li>A character identifier used in card notation (for example, 'h' for Hearts).</li>
 *   <li>A numeric index used internally by the application.</li>
 *   <li>A Unicode symbol representing the suit.</li>
 *   <li>A network code used for client-server communication.</li>
 * </ul>
 *
 * <p>
 * This enum also provides utility methods for converting network codes into
 * {@code Suit} instances and accessing each suit's properties.
 * </p>
 */
public enum Suit {
    
    /**
     * Hearts suit (♥).
     */
    HEARTS('h', 0, GameType.HEARTS),
    /**
     * Diamonds suit (♦).
     */
    DIAMONDS('d', 1, GameType.DIAMONDS),
    /**
     * Clubs suit (♣).
     */
    CLUBS('c', 2, GameType.CLUBS),
    /**
     * Spades suit (♠).
     */
    SPADES('s', 3, GameType.SPADES);
    /**
     * Character used to identify the suit in card notation.
     */
    private char _letra;
    /**
     * Numeric index used internally by the application to represent the suit.
     */
    private int _indx;
    /**
     * Unicode symbol representing the suit (♥, ♦, ♣, ♠).
     */
    private char _simbolo;
    /**
     * Network code used for client-server communication to represent the suit.
     */
    private int _networkCode;

    /**
     * Creates a suit with its associated notation character, internal index, and network code.
     * <p>
     * The corresponding Unicode symbol for the suit is also set based on the provided character.
     * </p>
     * @param letra
     * @param index
     * @param networkCode
     */
    private Suit(char letra, int index, int networkCode) {

        this._letra = letra;
        this._indx = index;
        this._networkCode = networkCode;
        switch (letra) {
        case 'h':
            _simbolo = '\u2665';
            break;
        case 'd':
            _simbolo = '\u2666';
            break;
        case 'c':
            _simbolo = '\u2663';
            break;
        case 's':
            _simbolo = '\u2660';
            break;
        }
    }
    /**
     * Returns the suit associated with the specified network code.
     * @param code
     * @return The corresponding {@code Suit} instance, or {@code null} if the code does not match any suit.
     */
    public static Suit getSuitFromCode(final int code) {
        
        switch (code) {
        case GameType.HEARTS:
            return Suit.HEARTS;
    
        case GameType.DIAMONDS:
            return Suit.DIAMONDS;

        case GameType.CLUBS:
            return Suit.CLUBS;

        case GameType.SPADES:
            return Suit.SPADES;

        default:
            return null;
        }
    }
    /**
     * Returns the character identifier for this suit, used in card notation.
     * @return The character representing the suit (e.g., 'h' for Hearts).
     */
    public char getLetra() {
        return _letra;
    }
    /**
     * Returns the Unicode symbol representing this suit.
     * @return The Unicode character for the suit (e.g., '♥' for Hearts).
     */
    public char getSimbolo() {
        return _simbolo;
    }
    /**
     * Returns the numeric index associated with this suit, used internally by the application.
     * @return The index of the suit (0 for Hearts, 1 for Diamonds, 2 for Clubs, 3 for Spades).
     */
    public int getIndex() {
        return _indx;
    }  
    /**
     * Returns the network code associated with this suit, used for client-server communication.
     * @return The network code of the suit (e.g., 0 for Hearts, 1 for Diamonds, 2 for Clubs, 3 for Spades).
     */
    public int getNetworkCode() {
        return _networkCode;
    }
}
