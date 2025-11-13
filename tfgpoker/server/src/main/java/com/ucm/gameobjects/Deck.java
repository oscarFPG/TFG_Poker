package com.ucm.gameobjects;

import java.util.Random;


public class Deck {

    private static int NUM_VALUES = 12;
    private static int NUM_SUITS = Suit.values().length;
    private static Suit[] SUITS = Suit.values();

    private Card[][] _deck;
    private Random _random;

    public Deck() {
        _random = new Random();
        initializeDeck();
    }

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

    public void printAllDeck() {

        for (int i = 0; i < SUITS.length; i++) {

            for (int j = 0; j < NUM_VALUES; j++) {
                if (_deck[i][j] == null) {
                    System.out.print("[xx]");
                }
                else {
                    System.out.print( _deck[i][j].toString() );
                }
            }
            System.out.println();
        }
    }

    public void retrieveCard(Card card) {

        int suitIndex = card.getSuit().getIndex();
        int valueIndex = card.getNumber() - 2;

        _deck[suitIndex][valueIndex].setAvailable(true); // la devolvemos al mazo --> indicamos que esta disponible
    }

    public void resetDeck(){

        for (int i = 0; i < NUM_SUITS; i++) {
            for (int j = 0; j < NUM_VALUES; j++) {
                _deck[i][j].setAvailable(true);
            }
        }
    }
    
}

