package com.ucm.gameobjects;

public class Card {

    private int _number;
    private Suit _suit; 
    private boolean _available;
    private boolean _sol;
            
    public Card (int number, Suit suit) {
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

    public String toString(){
        return String.format("[%c%s]", valueToString(), _suit.getLetra());
    }
    
    static public String FlippedDownCardToString(){
        return "[xx]";
    }
    
    static public String MissingCardToString(){
        return "[--]";
    }

    private char valueToString(){

        if(_number < 10)
            return Character.forDigit(_number, 11);

        switch (_number) {
            case 10:
                return 'T';
            case 11:
                return 'J';
            case 12:
                return 'Q';
            case 13:
                return 'K';
            default:
                return 'A';
        }
    }
    
    public int getNumber(){ return _number; }
    public Suit getSuit(){ return _suit; }
    public boolean getSol(){ return _sol; }
    public boolean getAvailable(){ return _available; }
}
