package hja.grupo3.practica1_hja.plays;

import java.util.Arrays;
import hja.grupo3.practica1_hja.game.Card;
import hja.grupo3.practica1_hja.misc.CardComparatorNumber;

/*
    Lo componen tres cartas del mismo valor. Entre dos tríos gana el que está formado por 
    cartas más altas. Si ambos tríos están formados por cartas del mismo valor, decide el 
    desempate la carta más alta de las dos restantes de que consta la mano, y si esas dos 
    cartas fueran iguales, se compararían las quintas cartas de cada mano.
*/

public class Three_of_a_Kind  extends Play {

     public Three_of_a_Kind (){
        this.name = "Three of a Kind of ";
    }
     
    private final String numberOrder = "23456789TJQKA";
    char highCard = 'X'; 
    
    @Override
    public boolean checkPlay(Card cards[]) {
        Card c[] = new Card[cards.length];
        
        CardComparatorNumber customComparator = new CardComparatorNumber();
  
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        Arrays.sort(c, customComparator);
         if (c.length < 3) return false;
        
         int it = 0;
         for (int i = 0; i < c.length - 1; i++){
             if (ok(c[i], c[i+1])) {
                 it++;
                 if (it == 2){
                     //Se supone que poker no puede darse porque ya se ha comprobado
                     highCard = c[i].getNumber();
                     c[i-1].setSol(true);
                     c[i].setSol(true);
                     c[i+1].setSol(true);
                     this.name += NumbertoString(highCard) + " (" + cardstoString(c) + ")";
                     return true;
                }
             }else {
                 it = 0;
             }
             
         }
        
        return false;
    }
    
    @Override
    public boolean checkDraw(Card[] cards) {
        return false;
    }
    
    @Override
    public char getHighCard() {
        return highCard;
    }
    
    private boolean ok (Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 0;
    }
}
