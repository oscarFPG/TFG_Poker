package hja.grupo3.practica2_hja.play;

import java.util.Arrays;
import hja.grupo3.practica2_hja.game.Card;
import hja.grupo3.practica2_hja.misc.CardComparatorNumber;

/*
    Para la escalera se precisan cinco cartas de orden consecutivo. Entre dos escaleras 
    gana la que sea más alta. Por otra parte, el As puede usarse para formar la escalera 
    A-K-Q-J-10, que sería una Escalera Real, y también la escalera 5-4-3-2-A.
*/
    
public class Straight  extends Play {
     public Straight(){
        this.name = "Straight";
    }
    private final String numberOrder = "23456789TJQKA";

    @Override
    public boolean checkPlay(Card cards[]) {
        //Crea un array de la misma longitud en el que "vuelca el orden"
        Card c[] = new Card[cards.length];
        
        //Crea el comparador
        CardComparatorNumber customComparator = new CardComparatorNumber();
        //Las coloca
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        //ordenar las cartas del mayor a menor
        Arrays.sort(c, customComparator);
        
        if (c.length < 5){
            return false;
        }
        
        for (int i = 0; i < c.length - 1; i++){
            //meter casos de escalera
                if(!ok(c[i], c[i+1])) return false;
        }
        
        for (int i = 0; i < c.length; i++){
            c[i].setSol(true);
        }
        //this.name += "(" + cardstoString(c) + ')';
        return true;
    }
    
    @Override
    public char getHighCard() {
        return 'X'; //no es necesario saber la carta alta en esta jugada
    }
    
    
    //Casos de la escalera
    private boolean ok(Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 1;
    }
    
    private boolean almost(Card c1, Card c2){
        return numberOrder.indexOf(c1.getNumber()) - numberOrder.indexOf(c2.getNumber())== 2;
    }
}
