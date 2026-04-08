package com.ucm.common.gameobjects;

import com.ucm.common.GameType;

public enum Suit {
    
    HEARTS('h', GameType.HEARTS), 
    DIAMONDS('d', GameType.DIAMONDS), 
    CLUBS('c', GameType.CLUBS), 
    SPADES('s', GameType.SPADES);

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
