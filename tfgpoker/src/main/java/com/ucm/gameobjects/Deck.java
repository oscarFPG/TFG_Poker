package com.ucm.gameobjects;

import java.util.Random;

public class Deck {
   
    private Card[][] _deck;
    private Random _random;
    
    private static final char[] NUMBERS = new char[] {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};
    

    public Deck() {
        _random = new Random();
        initializeDeck();
    }

    private void initializeDeck() {

        Suit[] SUITS = Suit.values();
        _deck = new Card[SUITS.length][NUMBERS.length];
        for (int i = 0; i < SUITS.length; i++) {
            for (int j = 0; j < NUMBERS.length; j++) {
                _deck[i][j] = new Card( NUMBERS[j], SUITS[i]);
            }
        }
    }

    public Card takeRandomCard() {
         Suit[] SUITS = Suit.values();
        int suit = _random.nextInt(SUITS.length);
        int value = _random.nextInt(NUMBERS.length);

        if (!_deck[suit][value].getAvailable()) { // si la carta no esta disponible en el mazo
            return takeRandomCard(); // vuelve a intentar si ya fue retirada
        }

        Card taken = _deck[suit][value];
        taken.setAvailable(false); // la quitamos del mazo --> indicamos que no esta disponible
        return taken;
    }

    public void printAllDeck() {
        Suit[] SUITS = Suit.values();
        for (int i = 0; i < SUITS.length; i++) {
            for (int j = 0; j < NUMBERS.length; j++) {
                if (_deck[i][j] == null) {
                    System.out.print("[xx]");
                } else {
                    System.out.print(_deck[i][j].toString());
                }
            }
            System.out.println();
        }
    }

    public void retrieveCard(Card card) {
         Suit[] SUITS = Suit.values();
        _deck[card.getSuit().getIndex()][card.getNumber()].setAvailable(true); // la devolvemos al mazo --> indicamos que esta disponible
    }
}
