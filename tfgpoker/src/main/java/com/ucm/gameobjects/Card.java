package com.ucm.gameobjects;

public class Card {

    private char _number;
    private Suit _suit; 
    private boolean _available;
    private boolean _sol;
            
    public Card (char number, Suit suit) {
        _number = number;
        _suit = suit;
        _sol = false;
        _available = true;
    } 
    
    
    public void setSol(boolean b){
        _sol = b;
    }

    public void setAvailable(boolean b){
        _available = b;
    }
    
    static public String FlippedDownCardToString(){
        return "[xx]";
    }
    
    static public String MissingCardToString(){
        return "[--]";
    }
    
    public String toString(){
        return String.format("[%c%s]", _number, _suit.getLetra());
    }
    
    
    public char getNumber(){ return _number; }
    public Suit getSuit(){ return _suit; }
    public boolean getSol(){ return _sol; }
    public boolean getAvailable(){ return _available; }
}
