package hja.grupo3.practica1_hja.plays;

import hja.grupo3.practica1_hja.game.Card;
import hja.grupo3.practica1_hja.misc.CardComparatorNumber;
import java.util.Arrays;

/*
    Esta jugada reúne 3 cartas de un valor y 2 de otro. Cuando se comparan dos fulls, 
    gana el que tiene el valor de las tres cartas más alto. Así pues, un full 7-7-7-2-2 
    supera a un full 5-5-5-A-A. En caso de que los tríos sean del mismo valor en dos manos, 
    gana la mano que tenga la pareja de cartas restante más alta, con lo que por ejemplo 
    7-7-7-A-A superaría a 7-7-7-K-K.
*/

public class Full_House extends Play {
    
    public Full_House(){
        this.name = "Full House";
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
        
        //COMPRUEBA THREE PRIMERO
        boolean ok = false;
         int it = 0;
         for (int i = 0; i < c.length - 1; i++){
             if (c[i].getNumber() == c[i+1].getNumber()) {
                 it++;
                 if (it == 2){
                     //Se supone que poker no puede darse porque ya se ha comprobado
                     highCard = c[i].getNumber();
                     c[i-1].setSol(true);
                     c[i].setSol(true);
                     c[i+1].setSol(true);
                     ok = true;
                }
             }else {
                 it = 0;
             }
             
         }
         
         //LUEGO COMPRUEBA PAIR
         for (int i = 0; i < c.length - 1; i++){
            if (c[i].getNumber() == c[i+1].getNumber() && !c[i].getSol() && !c[i+1].getSol() && ok){
                highCard = c[i].getNumber();
                c[i].setSol(true);
                c[i+1].setSol(true);
                this.name = this.name + " (" + cardstoString(c) + ")";
                return true;
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
    
}