package hja.grupo3.practica1_hja.plays;

import hja.grupo3.practica1_hja.game.Card;
import hja.grupo3.practica1_hja.misc.CardComparatorNumber;
import java.util.Arrays;

/*
    Dos cartas del mismo valor. Cuanto más alto es el valor de la pareja, más alto es su 
    ranking. En caso de igualdad, se recurre a la carta más alta.
*/

public class Pair  extends Play {
    private final String numberOrder = "23456789TJQKA";
    private char highCard = 'X'; //no se sabe aún cuál es la carta más alta
    
    public Pair() {
        this.name = "Pair of ";
    }
    
    /* Dos cartas del mismo valor. Cuanto más alto es el valor de la pareja, más alto es su 
    ranking. En caso de igualdad, se recurre a la carta más alta. */
    @Override
    public boolean checkPlay(Card cards[]) {
        Card c[] = new Card[cards.length];
        
        CardComparatorNumber customComparator = new CardComparatorNumber();
  
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        Arrays.sort(c, customComparator);
        
        boolean x = false;
        for (int i = 0; i < c.length - 1; i++){
            if (ok(c[i], c[i+1]) && !x){
                highCard = c[i].getNumber();
                c[i].setSol(true);
                c[i+1].setSol(true);
                this.name += NumbertoString(highCard) + " (" + cardstoString(c) + ")";
                return true;
            } else if (ok(c[i], c[i + 1])){ // Se supone que un trio no hay porque es una clase que va antes
               return true;
            }
        }
        
        return false;
        
    }
    
    // En este caso no es posible el Draw.
    @Override
    public boolean checkDraw(Card[] cards) {return false;}
    
    @Override
    public char getHighCard() {return highCard;}
    
    private boolean ok (Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 0;
    }
    
}
