package com.ucm.server.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.logic.Game;


/**
 * This class is the responsible of managing all the {@link Card} objects of the
 * game.
 * Every interacting with the cards of the game should be done through this
 * class, such as taking a card or retrieving it.
 */
public class Deck {


    private static final int DEBUG_SEED = 123;

    /**
     * Number of different values in the deck (2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K,
     * A)
     */
    private static int NUM_VALUES = 12;

    /**
     * Number of different suits in the deck (Hearts, Diamonds, Clubs, Spades)
     */
    private static int NUM_SUITS = Suit.values().length;

    /**
     * Array containing all the suits in the deck.
     * Check {@link Suit} enum for more information
     */
    private static Suit[] SUITS = Suit.values();

    /**
     * Bidimensional array representing the deck of cards.
     * First dimension represents the suits, second dimension represents the values.
     */
    private Card[][] _deck;

    /**
     * {@link Random} object used to take random cards from the deck.
     */
    private Random _random;

    /**
     * Class constructor, initializes the deck and the random object.
     */
    public Deck() {

        if(Game.DEBUG)
            _random = new Random(DEBUG_SEED);
        else
            _random = new Random();
        
        initializeDeck();
    }

    /**
     * Initializes the deck of cards.
     * Creates a new {@link Card} object for each combination of suit and value.
     */
    private void initializeDeck() {

        _deck = new Card[NUM_SUITS][NUM_VALUES];

        for (int i = 0; i < NUM_SUITS; i++) {

            int value = 2;
            for (int j = 0; j < NUM_VALUES; j++) {
                _deck[i][j] = new Card(value, SUITS[i]);
                ++value;
            }
        }
    }

    /**
     * Takes a random card from the deck.
     * This method never retrieves a Card that has already been taken.
     * 'Picking' a card from the deck is done by setting its availability to false,
     * it is <b>never</b> removed from the deck.
     * Check {@link Card} class for more information about the availability of a
     * card and the methods related to it.
     * 
     * @return
     */
    public Card takeRandomCard() {

        int suit = _random.nextInt(SUITS.length);
        int value = _random.nextInt(NUM_VALUES);

        if (!_deck[suit][value].getAvailable()) { // si la carta no esta disponible en el mazo
            return takeRandomCard(); // vuelve a intentar si ya fue retirada
        }

        Card taken = _deck[suit][value];
        taken.setAvailable(false); // la quitamos del mazo --> indicamos que no esta disponible
        return taken;
    }

    /**
     * Retrieves a card to the deck, making it available again.
     * It must be taken previously from the deck using {@link #takeRandomCard()}
     * method, otherwise it may cause inconsistencies in the deck.
     * 
     * @param card to be retrieved to the deck.
     */
    public void retrieveCard(Card card) {

        int suitIndex = card.getSuit().getIndex();
        int valueIndex = card.getNumber() - 2;

        _deck[suitIndex][valueIndex].setAvailable(true); // la devolvemos al mazo --> indicamos que esta disponible
    }

    /**
     * Resets the deck to its initial state, making all the cards available again.
     * All cards must be retrieved before calling this method to avoid
     * inconsistencies in the deck.
     * Check {@link #retrieveCard(Card)} method of more information.
     */
    public void resetDeck() {

        for (int i = 0; i < NUM_SUITS; i++) {
            for (int j = 0; j < NUM_VALUES; j++) {
                _deck[i][j].setAvailable(true);
            }
        }
    }

    /**
     * 
     * 
     * 
     * @return 
     */
    public List<Card> getAvailableCards() {

        List<Card> available = new ArrayList<>(NUM_SUITS * NUM_VALUES);

        for (int i = 0; i < NUM_SUITS; i++) {
            for (int j = 0; j < NUM_VALUES; j++) {

                if (_deck[i][j].getAvailable()) {
                    available.add(_deck[i][j]);
                }
            }
        }

        return available;
    }

}
