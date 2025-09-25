package com.ucm.gameobjects;

public enum Suit {
    HEARTS('h'), DIAMONDS('d'), CLUBS('c'), SPADES('s');

    private char _letra;

    Suit(char letra) {
        this._letra = letra;
    }
    
    public char getLetra() {
        return _letra;
    }
}
