package hja.grupo3.practica1_hja.plays;

import java.util.Arrays;
import hja.grupo3.practica1_hja.game.Card;
import hja.grupo3.practica1_hja.misc.CardComparatorNumber;

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
    public boolean checkDraw(Card[] cards) {
        Card c[] = new Card [cards.length];
        CardComparatorNumber customComparator = new CardComparatorNumber();
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        Arrays.sort(c, customComparator);
        if (c.length < 5){
            return false;
        }
        
        int contTrue = 0, contAlmost = 0;
        //boolean x = false, y = false;
        
        for (int i = 0; i < c.length - 1; i++){
            if (ok(c[i], c[i + 1])){
                contTrue++;
            }else if (almost(c[i], c[i + 1])){
                contAlmost++;
            }
        }
        
        if (contTrue == 3){
            this.draw_name = this.name + " Open-Ended";
            return true;
        } //open-ended
        else if (contTrue == 2 && contAlmost >= 1){
            this.draw_name = this.name + " Gutshot";
            return true;
        } //gutshot 
  
         
        return false;
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
