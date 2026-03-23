package com.ucm.server.gameobjects;


public enum Suit {
    HEARTS('h', 0), DIAMONDS('d', 1), CLUBS('c', 2), SPADES('s', 3);

    private char _letra;
    private int _indx;
    private char simbolo;

    Suit(char letra, int index) {
        this._letra = letra;
        this._indx = index;
        switch (letra) {
        case 'h':
            simbolo = '\u2665';
            break;
        case 'd':
            simbolo = '\u2666';
            break;
        case 'c':
            simbolo = '\u2663';
            break;
        case 's':
            simbolo = '\u2660';
            break;
        }
    }
    
    public char getLetra() {
        return _letra;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public int getIndex() {
        return _indx;
    }  
}
