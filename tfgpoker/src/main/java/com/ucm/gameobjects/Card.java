package com.ucm.gameobjects;

public class Card {

    private char number;
    private char suit; 
    private boolean active;
    private boolean sol;
            
    public Card (char number ,char suit) {
        this.number = number;
        this.suit = suit;
        this.sol = false;
        this.active = true;
    } 
    
    public char getNumber(){
        return number;
    }
    
    public char getSuit(){
        return suit;
    }
    
    public void setSol(boolean b){
        sol = b;
    }
    public void setActive(boolean b){
        active = b;
    }
  
    public boolean getSol(){
        return sol;
    }
    
     public boolean getActive(){
        return active;
    }
}
