package hja.grupo3.practica2_hja.play;

import hja.grupo3.practica2_hja.game.Card;
import hja.grupo3.practica2_hja.misc.CardComparatorNumber;
import java.util.Arrays;

/*
    Una doble pareja está formada por dos cartas del mismo valor en combinación con otras
    dos cartas también de un mismo valor, pero diferente al valor de las dos primeras. 
    Cuando dos manos muestran doble pareja, gana la mano cuya pareja es más alta. Como 
    siempre, en caso de empate decide la carta más alta.
*/

public class Two_Pairs  extends Play {
    
    public Two_Pairs(){
        this.name = "Two Pairs of ";
    }
    private final String numberOrder = "23456789TJQKA";
    char highCard = 'X'; //no se sabe aún cuál es la carta más alta
    char secondHighCard = 'Y';

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
                highCard = c[i].getNumber();
            } else if (ok(c[i], c[i + 1]) && x){ // Se supone que un trio no hay porque es una clase que va antes
                secondHighCard = c[i].getNumber();
                c[id].setSol(true);
                c[id + 1].setSol(true);
                c[i].setSol(true);
                c[i+1].setSol(true);
                this.name += NumbertoString(highCard) + " and " + NumbertoString(secondHighCard) + " with (" + cardstoString(c) + ")";
               return true;
            }
        }
        
        return false;
    }
    
    @Override
    public char getHighCard() {
        return highCard; //no es necesario saber la carta alta en esta jugada
    }
    
    private boolean ok (Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 0;
    }
    
}
