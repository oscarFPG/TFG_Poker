package com.ucm.server.gameobjects;

/**
 * Simple class that represents a poker card in the game.
 * It has a value such as Ace, 2, 3, ..., 10, Jack, Queen and King.
 * It has a {@link Suit} such as Hearts, Diamonds, Clubs and Spades.
 * These are created and taken by the {@link Deck} class and can be placed on
 * the table
 * or in the players' hands.
 * They can be flipped down or up to reveal their value and suit.
 */
public class Card {

    /**
     * The number of the card, from 1 to 13, where 1 is Ace, 11 is Jack, 12 is Queen
     * and 13 is King.
     */
    private int _number;

    /**
     * The suit of the card, which can be Hearts, Diamonds, Clubs and Spades.
     * To get more information about the Suits you can check the {@link Suit} class.
     */
    private Suit _suit;

    /**
     * Indicates whether the card is available to be picked up from the deck or not.
     * A card can be unavailable if it has been picked up by a player or if it has
     * been placed on the table.
     */
    private boolean _available;

    /**
     * Not used in the current implementation.
     */
    private boolean _sol;

    /**
     * Class constructor that creates a card with a number and a suit.
     * 
     * @param number from 1 to 13, where 1 is Ace, 11 is Jack, 12 is Queen and 13 is
     *               King.
     * @param suit   the {@link Suit} of the card, which can be Hearts, Diamonds,
     *               Clubs and Spades.
     */
    public Card(int number, Suit suit) {
        _number = number;
        _suit = suit;
        _sol = false;
        _available = true;
    }

    /**
     * @deprecated Not used in the current implementation
     * @param b the new boolean value for the sol property
     */
    public void setSol(boolean b) {
        _sol = b;
    }

    /**
     * Sets the availability of the card.
     * This boolean must be set to false when the card is picked up by a player or
     * when it is placed on the table, and true when it is returned to the deck.
     * If a card is not available, it cannot be picked up by a player or placed on
     * the table.
     * 
     * @param b the new boolean value for the available property
     */
    public void setAvailable(boolean b) {
        _available = b;
    }

    /**
     * Overrides the {@link Object#toString()} method to return a {@link String}
     * representation of the card.
     * The Ace of Spades card is represented asg the {@link String} [As].
     * You can check the {@link Suit} class to see the letters that represent each
     * suit and the {@link #valueToString()} method to see how the numbers are
     * represented as
     * characters.
     * 
     * @return {@link String} representation of the card
     */
    public String toString() {
        return String.format("[%c%s]", valueToString(), _suit.getLetra());
    }

    /**
     * Returns a string representation of a flipped down card, which is
     * represented as [xx]
     * 
     * @return {@link String}
     */
    static public String FlippedDownCardToString() {
        return "[xx]";
    }

    /**
     * Returns a string representation of a missing card, which is
     * represented as [--]
     * This is only useful when a card is missing from the deck or the table and the
     * state of the {@link Deck} or {@link Game} are being printed to the console.
     * 
     * @return {@link String} representation of the missing card
     */
    static public String MissingCardToString() {
        return "[--]";
    }

    /**
     * Returns a character representation of the card's number.
     * The numbers from 2 to 9 are represented as their corresponding digits while
     * the Ace, 10, Jack, Queen and King are represented as A, T, J, Q and K
     * respectively.
     * 
     * @return char
     */
    private char valueToString() {

        if (_number < 10)
            return Character.forDigit(_number, 11);

        switch (_number) {
            case 10:
                return 'T';
            case 11:
                return 'J';
            case 12:
                return 'Q';
            case 13:
                return 'K';
            default:
                return 'A';
        }
    }

    /**
     * Return the raw number of the card, from 1 to 13, where 1 is Ace, 11 is Jack,
     * 12 is Queen and 13 is King.
     * 
     * @return int
     */
    public int getNumber() {
        return _number;
    }

    /**
     * Returns the {@link Suit} of the card, which can be Hearts, Diamonds, Clubs
     * and Spades.
     * 
     * @return {@link Suit}
     */
    public Suit getSuit() {
        return _suit;
    }

    /**
     * Returns the boolean value of the sol property, which is not used in the
     * current implementation.
     * 
     * @deprecated Not used in the current implementation
     * @return boolean
     */
    public boolean getSol() {
        return _sol;
    }

    /**
     * Return the boolean value of the available property, which indicates whether
     * the card is available to be picked up from the deck or not.
     * 
     * @return boolean
     */
    public boolean getAvailable() {
        return _available;
    }
}
