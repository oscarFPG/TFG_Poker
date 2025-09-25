package com.ucm.gameobjects;

public class Card {

    private char _number;
    private char _suit; 
    private boolean _available;
    private boolean _sol;
            
    public Card (char number ,char suit) {
        this._number = number;
        this._suit = suit;
        this._sol = false;
        this._available = true;
    } 
    
    public char getNumber(){
        return _number;
    }
    
    public char getSuit(){
        return _suit;
    }
    
    public void setSol(boolean b){
        _sol = b;
    }
    public void setAvailable(boolean b){
        _available = b;
    }
  
    public boolean getSol(){
        return _sol;
    }
    
     public boolean getAvailable(){
        return _available;
    }
}
