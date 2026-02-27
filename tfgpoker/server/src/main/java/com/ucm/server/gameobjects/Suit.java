package com.ucm.server.gameobjects;


/**
 * Enum representing the four suits in a standard deck of playing cards.
 * Each suit has a corresponding character and an index for easy identification.
 * - HEARTS: Represented by 'h' and index 0
 * - DIAMONDS: Represented by 'd' and index 1
 * - CLUBS: Represented by 'c' and index 2
 * - SPADES: Represented by 's' and index 3
 */
public enum Suit {
    HEARTS('h', 0), DIAMONDS('d', 1), CLUBS('c', 2), SPADES('s', 3);

    /**
     * The character representation of the suit (e.g., 'h' for hearts).
     */
    private char _letra;

    /**
     * The index of the suit (e.g., 0 for hearts, 1 for diamonds, etc.).
     */
    private int _indx;

    /**
     * Constructor for the Suit enum.
     * @param letra The character representation of the suit.
     * @param index The index of the suit.
     */
    Suit(char letra, int index) {
        this._letra = letra;
        this._indx = index;
    }
    
    /**
     * Returns the character representation of the suit.
     * @return The character representation of the suit.
     */
    public char getLetra() {
        return _letra;
    }

    /**
     * Returns the index of the suit.
     * @return The index of the suit.
     */
    public int getIndex() {
        return _indx;
    }  
}
