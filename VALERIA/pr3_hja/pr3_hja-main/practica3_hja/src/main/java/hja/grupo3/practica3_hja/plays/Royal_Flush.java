package hja.grupo3.practica3_hja.plays;

import java.util.Arrays;
import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.game.Suit;
import hja.grupo3.practica3_hja.misc.CardComparatorSuit;

 /*
    La mejor jugada del poker. Comprende las cartas 10, J, Q, K y A del mismo palo. 
    Todo jugador tiene derecho a una de éstas a lo largo de su vida.
 */
    
public class Royal_Flush extends Play {
    
    public Royal_Flush(){
        this.name = "Royal Flush";
    }
    
    @Override
    public boolean checkPlay(Card cards[]) {
        
        Card c[] = new Card[cards.length];        
        
        CardComparatorSuit customComparator = new CardComparatorSuit();

        for (int i = 0; i < cards.length; ++i) {
            c[i] = cards[i];
        }
        
        Arrays.sort(c, customComparator);
        
        char lastSuit = c[0].getSuit();
        char lastNumber = 'X';
           
        boolean T = false;
        
        for (int i = 0; i < c.length; ++i) {
            if (c[i].getSuit() == lastSuit) {
                if (lastNumber == 'X') {
                    if (c[i].getNumber() == 'A') lastNumber = c[i].getNumber();
                    else lastNumber = 'X'; //no es posible, pero las siguientes podrían
                }   
                else if (lastNumber == 'A') {
                    if (c[i].getNumber() == 'K') lastNumber = c[i].getNumber();
                    else lastNumber = 'X'; //no es posible, pero las siguientes podrían
                }           
                else if (lastNumber == 'K') {
                    if (c[i].getNumber() == 'Q') lastNumber = c[i].getNumber();
                    else lastNumber = 'X'; //no es posible, pero las siguientes podrían
                }
                else if (lastNumber == 'Q') {
                    if (c[i].getNumber() == 'J') lastNumber = c[i].getNumber();
                    else lastNumber = 'X'; //no es posible, pero las siguientes podrían
                }
                else if (lastNumber == 'J') {
                    if (c[i].getNumber() == 'T') return true; //hay escalera real
                    else lastNumber = 'X'; //no es posible, pero las siguientes podrían
                }
            }
            else {
                lastSuit = c[0].getSuit(); //otro palo
                if (c[i].getNumber() == 'A') lastNumber = c[i].getNumber();
                else lastNumber = 'X'; //no es posible, pero las siguientes podrían
            }
        }
     
        return false;
    }
    
    @Override
    public char[] getHighCard() {
        char[] hc = {'X'};
        return hc;
    }
    
}
