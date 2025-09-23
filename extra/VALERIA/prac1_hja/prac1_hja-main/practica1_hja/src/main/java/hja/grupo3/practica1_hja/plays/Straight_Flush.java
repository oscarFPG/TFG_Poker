package hja.grupo3.practica1_hja.plays;

import hja.grupo3.practica1_hja.game.Card;
import java.util.Arrays;
import hja.grupo3.practica1_hja.game.Suit;
import hja.grupo3.practica1_hja.misc.CardComparatorSuit;

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
    public boolean checkDraw(Card[] cards) {
      
        if (checkPlay(cards)){
            this.draw_name = this.name;
            return true;
        }
        
        Card c[] = new Card[cards.length];        
        
        CardComparatorSuit customComparator = new CardComparatorSuit();

        for (int i = 0; i < cards.length; ++i) {
            c[i] = cards[i];
        }
        
        Arrays.sort(c, customComparator);
       
        int indexIni = numberOrder.indexOf(c[0].getNumber());
        int indexFin = numberOrder.indexOf(c[c.length-1].getNumber());
                
        char ini = (indexIni == 0) ? 'A' : numberOrder.charAt(indexIni - 1); 
        char fin = (indexFin == numberOrder.length()-1) ? '2' : numberOrder.charAt(indexFin + 1); ;
          
        char openended[] = {ini, fin};     
        char n[] = {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        Card cDraw[] = new Card[cards.length+1];
        
        for (int i = 0; i < cards.length; i++){
            cDraw[i] = cards[i];
        }
        
        for (int i = 0; i < 4; ++i){
            for (int j = 0; j < openended.length; ++j) {
                cDraw[cards.length] = new Card(Suit.getSuit(i), openended[j]);
                if (checkPlay(cDraw)){
                    this.draw_name = this.name + " Open-Ended";
                    return true;
                } //hay open-ended
            }
        }
        
        for (int i = 0; i < 4; ++i){
            for (int j = indexIni+1; j < indexFin; ++j) {
                cDraw[cards.length] = new Card(Suit.getSuit(i), n[j]);
                if (checkPlay(cDraw)){
                   this.draw_name = this.name + " Gutshot";
                   return true; 
                } //hay gutshot
            }
        }
        
        return false;
    }
    
    @Override
    public char getHighCard() {
        return highCard; 
    }
    
}
