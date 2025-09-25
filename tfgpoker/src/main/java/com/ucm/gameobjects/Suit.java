package com.ucm.gameobjects;

public enum Suit {
    HEARTS('h', 0), DIAMONDS('d', 1), CLUBS('c', 2), SPADES('s', 3);

    private char _letra;
    private int _indx;

    Suit(char letra, int index) {
        this._letra = letra;
        this._indx = index;
    }
    
    public char getLetra() {
        return _letra;
    }

    public int getIndex() {
        return _indx;
    }  
}
