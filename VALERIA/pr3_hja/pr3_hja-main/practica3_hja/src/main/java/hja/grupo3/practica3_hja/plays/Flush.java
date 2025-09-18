package hja.grupo3.practica3_hja.plays;

import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.misc.CardComparatorNumber;
import java.util.Arrays;

 /*
    El color lo forman cinco cartas no consecutivas del mismo palo. Gana el desempate 
    entre dos colores aquel que tenga la carta más alta.
*/

public class Flush extends Play {

     /*El color lo forman cinco cartas no consecutivas del mismo palo. Gana el desempate 
    entre dos colores aquel que tenga la carta más alta.*/
    private char high_Card = 'X'; // no se sabe aún

    public Flush(){
        this.name = "Flush";
        this.draw_name = "Flush";
    }
    @Override
    public boolean checkPlay(Card cards[]) {
        if (0 != numSuits(cards,5)) {
            for (Card c : cards) {c.setSol(true);}
            //this.name += " with (" + cardstoString(cards) + ")";
            return true;
        }
        return false;
    }
      
    public char numSuits(Card[] cards, int count) {
        int nH = 0, nD = 0, nC = 0, nS = 0;
        
        for (Card c :  cards) {
            switch (c.getSuit()) {
                case 'h' -> nH++;
                case 'd' -> nD++;
                case 'c' -> nC++;
                case 's' -> nS++;
            }
        }
        
        Card c[] = new Card[cards.length];
        CardComparatorNumber customComparator = new CardComparatorNumber();
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        Arrays.sort(c, customComparator);
        high_Card = c[0].getNumber();
        
        if (nH >= count) {
            return 'h';
        }
        else if (nD >= count) {
            return 'd';
        }
        else if (nC >= count) {
            return 'c';
        }
        else if (nS >= count) {
            return 's';
        }
        
        return 0;
    }

    @Override
    public char[] getHighCard() {
        char[] hc = {high_Card};
        return hc;
    }
    
}
