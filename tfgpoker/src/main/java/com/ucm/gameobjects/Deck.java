package com.ucm.gameobjects;

import java.util.Random;

public class Deck {
    private static final int NUM_SUITS = 4; // Ajusta según tu definición
    private static final int NUM_VALUES = 13; // Ajusta según tu definición (2–14 si usas As=14)

    private Card[][] fullDeck;
    private Random random;
    private final String numberOrder = "23456789TJQKA";

    public Deck() {
        fullDeck = new Card[NUM_SUITS][NUM_VALUES];
        random = new Random();
        initializeDeck();
    }

    private void initializeDeck() {
        String suitOrder = "hdcs";
        for (int i = 0; i < NUM_SUITS; i++) {
            for (int j = 2; j < NUM_VALUES + 2; j++) {
                fullDeck[i][j - 2] = new Card(numberOrder.charAt(j), suitOrder.charAt(i));
            }
        }
    }

    public Card takeRandomCard() {
        int suit = random.nextInt(NUM_SUITS);
        int value = random.nextInt(NUM_VALUES);

        if (fullDeck[suit][value] == null) {
            return takeRandomCard(); // vuelve a intentar si ya fue retirada
        }

        Card taken = fullDeck[suit][value];
        fullDeck[suit][value] = null; // la quitamos del mazo
        return taken;
    }

    public void printAllDeck() {
        for (int i = 0; i < NUM_SUITS; i++) {
            for (int j = 0; j < NUM_VALUES; j++) {
                if (fullDeck[i][j] == null) {
                    System.out.print("[xx]");
                } else {
                    System.out.print(fullDeck[i][j].toString());
                }
            }
            System.out.println();
        }
    }

    public void retrieveCard(Card card) {
        // La primera carta (2) se guarda en posición 0, la 3 en la 1, etc.
        int suit = card.getSuit();
        int valueIndex = card.getNumber() - 2;
        fullDeck[suit][valueIndex] = card;
    }
}
