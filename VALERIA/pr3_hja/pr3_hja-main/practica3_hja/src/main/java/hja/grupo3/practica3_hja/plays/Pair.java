package hja.grupo3.practica3_hja.plays;

import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.misc.CardComparatorNumber;
import java.util.Arrays;

/*
    Dos cartas del mismo valor. Cuanto más alto es el valor de la pareja, más alto es su 
    ranking. En caso de igualdad, se recurre a la carta más alta.
*/

public class Pair extends Play {
    private final String numberOrder = "23456789TJQKA";
    private char highCardPair = 'X';
    private char highFirstCard = 'X';
    private char highSecondCard = 'X';
    private char highThreeCard = 'X';
    
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
                highCardPair = c[i].getNumber();
                c[i].setSol(true);
                c[i+1].setSol(true);
                this.name += NumbertoString(highCardPair) + " (" + cardstoString(c) + ")";
                for (int j = 0; j < c.length; ++j) {
                    if (highFirstCard == 'X' && c[j].getNumber() != highCardPair) {
                        highFirstCard = c[j].getNumber();
                    }
                    else if (highFirstCard != 'X' && highSecondCard == 'X' && c[j].getNumber() != highCardPair) {
                        highSecondCard = c[j].getNumber();
                    }
                    else if (highSecondCard != 'X' && highThreeCard == 'X' && c[j].getNumber() != highCardPair) {
                        highThreeCard = c[j].getNumber();
                        break;
                    }
                }
                return true;
            } else if (ok(c[i], c[i + 1])){ // Se supone que un trio no hay porque es una clase que va antes
                for (int j = 0; j < c.length; ++j) {
                    if (highFirstCard == 'X' && c[j].getNumber() != highCardPair) {
                        highFirstCard = c[j].getNumber();
                    }
                    else if (highFirstCard != 'X' && highSecondCard == 'X' && c[j].getNumber() != highCardPair) {
                        highSecondCard = c[j].getNumber();
                    }
                    else if (highSecondCard != 'X' && highThreeCard == 'X' && c[j].getNumber() != highCardPair) {
                        highThreeCard = c[j].getNumber();
                        break;
                    }
                }
                return true;
            }
        }
        
        
        
        return false;
        
    }
    
    @Override
    public char[] getHighCard() {
        char[] hc = {highCardPair, highFirstCard, highSecondCard, highThreeCard};
        return hc;
    }
    
    private boolean ok (Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 0;
    }
    
}
