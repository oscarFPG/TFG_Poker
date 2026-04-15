package com.ucm.common.gameobjects;

import com.ucm.common.GameType;

public enum Suit {
    
    HEARTS('h', 0, GameType.HEARTS), 
    DIAMONDS('d', 1, GameType.DIAMONDS), 
    CLUBS('c', 2, GameType.CLUBS), 
    SPADES('s', 3, GameType.SPADES);

    private char _letra;
    private int _indx;
    private char _simbolo;
    private int _networkCode;

    private Suit(char letra, int index, int networkCode) {

        this._letra = letra;
        this._indx = index;
        this._networkCode = networkCode;
        switch (letra) {
        case 'h':
            _simbolo = '\u2665';
            break;
        case 'd':
            _simbolo = '\u2666';
            break;
        case 'c':
            _simbolo = '\u2663';
            break;
        case 's':
            _simbolo = '\u2660';
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
        return _simbolo;
    }

    public int getIndex() {
        return _indx;
    }  

    public int getNetworkCode() {
        return _networkCode;
    }
}