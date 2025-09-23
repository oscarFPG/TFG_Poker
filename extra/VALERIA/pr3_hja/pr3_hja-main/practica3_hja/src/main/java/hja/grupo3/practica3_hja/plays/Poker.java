package hja.grupo3.practica3_hja.plays;

import java.util.Arrays;
import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.misc.CardComparatorNumber;

/*
    Cuatro cartas del mismo valor. Cuanto más alto es el valor de estas cuatro cartas, 
    más alto es el ranking de la mano. En caso de empate, posible en las variantes de 
    póquer que usan cartas comunitarias, gana la mano cuya quinta carta sea más alta.
*/

public class Poker extends Play {
    
    public Poker(){
        this.name = "Poker of ";
    }
    char highCardPoker = 'X'; //no se sabe aún cuál es la carta más alta
    char highCard = 'X';
    private final String numberOrder = "23456789TJQKA";

    @Override
    public boolean checkPlay(Card cards[]) {
        Card c[] = new Card[cards.length];        
        
        CardComparatorNumber customComparator = new CardComparatorNumber();

        for (int i = 0; i < cards.length; ++i) {
            c[i] = cards[i];
        }
        
        Arrays.sort(c, customComparator);
        
        char suit = c[0].getSuit();
        int counterPoker = 1;
        
        char n = c[0].getNumber();        
        
        
   
        int it = 0;
        for (int i = 0; i < c.length - 1; i++){
             if (ok(c[i], c[i+1])) {
                it++;
                if (it == 3){
                    highCardPoker = c[i].getNumber();
                    c[i-2].setSol(true);
                    c[i-1].setSol(true);
                    c[i].setSol(true);
                    c[i+1].setSol(true);
                    if (c[0].getNumber() != highCardPoker)
                        highCard = c[0].getNumber();
                    else
                        highCard = c[4].getNumber();
                    
                    this.name += NumbertoString(highCardPoker) + " (" + cardstoString(c) + ")";
                    return true;
                }
             }else {
                 it = 0;
             }
             
        }
        
        return false;
    }
    
    @Override
    public char[] getHighCard() {
        char[] hc = {highCardPoker, highCard};
        return hc;
    }
    
    private boolean ok (Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 0;
    }
}
