package hja.grupo3.practica1_hja.game;

public class Card {

    private char number;
    private char suit; 
    
    private boolean sol;
            
    public Card (char number ,char suit) {
        this.number = number;
        this.suit = suit;
        this.sol = false;
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
    
    public boolean getSol(){
        return sol;
    }
}
