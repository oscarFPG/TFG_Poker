package hja.grupo3.practica3_hja.plays;

import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.misc.CardComparatorNumber;
import java.util.Arrays;

/*
    Una doble pareja está formada por dos cartas del mismo valor en combinación con otras
    dos cartas también de un mismo valor, pero diferente al valor de las dos primeras. 
    Cuando dos manos muestran doble pareja, gana la mano cuya pareja es más alta. Como 
    siempre, en caso de empate decide la carta más alta.
*/

public class Two_Pairs extends Play {
    
    public Two_Pairs(){
        this.name = "Two Pairs of ";
    }
    private final String numberOrder = "23456789TJQKA";
    char highCard = 'X'; //no se sabe aún cuál es la carta más alta
    char secondHighCard = 'Y';
    char firsthighCard = 'X';

    @Override
    public boolean checkPlay(Card cards[]) {
        
        Card c[] = new Card[cards.length];
        
        CardComparatorNumber customComparator = new CardComparatorNumber();
  
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        Arrays.sort(c, customComparator);
         if (c.length < 4) return false; //Si no hay 4 cartas no puede haber doble parejas
        
        boolean x = false;
        int id = 0;
        for (int i = 0; i < c.length - 1; i++){
            if (ok(c[i], c[i+1]) && !x){
                x = true;
                id = i;
                firsthighCard = c[i].getNumber();
            } else if (ok(c[i], c[i + 1]) && x){ // Se supone que un trio no hay porque es una clase que va antes
                secondHighCard = c[i].getNumber();
                c[id].setSol(true);
                c[id + 1].setSol(true);
                c[i].setSol(true);
                c[i+1].setSol(true);
                this.name += NumbertoString(firsthighCard) + " and " + NumbertoString(secondHighCard) + " with (" + cardstoString(c) + ")";
                
                for (int j = 0; j < c.length; ++j) {
                    if (highCard == 'X' && c[j].getNumber() != firsthighCard && c[j].getNumber() != secondHighCard) {
                        highCard = c[j].getNumber();
                        break;
                    }
                }
                       
                if (numberOrder.indexOf(firsthighCard) < numberOrder.indexOf(secondHighCard)) {
                    char aux = secondHighCard;
                    secondHighCard = firsthighCard;
                    firsthighCard = aux;
                }
                    
                
               return true;
            }
        }
        
        return false;
    }
    
    @Override
    public char[] getHighCard() {
        char[] hc = {firsthighCard, secondHighCard, highCard};
        return hc;
    }
    
    private boolean ok (Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 0;
    }
    
}
