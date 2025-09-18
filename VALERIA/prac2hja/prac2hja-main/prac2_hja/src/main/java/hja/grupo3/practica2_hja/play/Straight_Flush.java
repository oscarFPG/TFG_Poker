package hja.grupo3.practica2_hja.play;

import hja.grupo3.practica2_hja.game.Card;
import java.util.Arrays;
import hja.grupo3.practica2_hja.game.Suit;
import hja.grupo3.practica2_hja.misc.CardComparatorSuit;

/*
    Cinco cartas de orden consecutivo del mismo palo. Cuanto más alta sea la carta más 
    alta de la escalera, mejor es el ranking de la mano. En este ejemplo la carta más 
    alta es el 10; vencería a una escalera de color con un 9 de carta más alta.
*/

public class Straight_Flush extends Play {

    private final String numberOrder = "23456789TJQKA";
    char highCard = 'X'; //no se sabe aún cuál es la carta más alta
    
    public Straight_Flush(){
        this.name = "Straight Flush";
    }
    @Override
    public boolean checkPlay(Card cards[]) {
        Card c[] = new Card[cards.length];        
        
        CardComparatorSuit customComparator = new CardComparatorSuit();

        for (int i = 0; i < cards.length; ++i) {
            c[i] = cards[i];
        }
        
        Arrays.sort(c, customComparator);
        
        char suit = c[0].getSuit();
        int counterFlush = 1;
        
        char highAux = c[0].getNumber();
        
        for (int i = 1; i < cards.length; ++i) {
            if (numberOrder.indexOf(c[i-1].getNumber()) - numberOrder.indexOf(c[i].getNumber()) > 1 || suit != c[i].getSuit()) {
                suit = c[i].getSuit();
                highAux = c[i].getNumber();
                counterFlush = 1;
            }
            else if (numberOrder.indexOf(c[i-1].getNumber()) - numberOrder.indexOf(c[i].getNumber()) == 1 && suit == c[i].getSuit()) {
                counterFlush++;
            }
            else return false;
            
            if (counterFlush == 5) {
                highCard = highAux;
                return true;
            }
        }
        
        return false;
    }
   
    @Override
    public char getHighCard() {
        return highCard; 
    }
    
}
