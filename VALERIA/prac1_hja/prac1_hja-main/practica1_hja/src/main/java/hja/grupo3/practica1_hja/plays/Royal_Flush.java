package hja.grupo3.practica1_hja.plays;

import java.util.Arrays;
import hja.grupo3.practica1_hja.game.Card;
import hja.grupo3.practica1_hja.game.Suit;
import hja.grupo3.practica1_hja.misc.CardComparatorSuit;

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
    public boolean checkDraw(Card[] cards) {
        
        if (checkPlay(cards)) {
            this.draw_name = this.name;
            return false;
        } // return 3 - no debería pasar porque ya es la mayor prioridad
        
        char openended[] = {'A', 'T'};
        char gutshot[] = {'J', 'Q', 'K'};
        
        Card cDraw[] = new Card[cards.length+1];
        
        for (int i = 0; i < cards.length; i++){
            cDraw[i] = cards[i];
        }
        
        for (int i = 0; i < 4; ++i){
            for (int j = 0; j < openended.length; ++j) {
                cDraw[cards.length] = new Card(Suit.getSuit(i), openended[j]);
                if (checkPlay(cDraw)) {
                    this.draw_name = this.name + " Open-Ended";
                    return true; // return 1 - hay open-ended
                }
            }
        }
        
        for (int i = 0; i < 4; ++i){
            for (int j = 0; j < gutshot.length; ++j) {
                cDraw[cards.length] = new Card(Suit.getSuit(i), gutshot[j]);
                if (checkPlay(cDraw)){
                    this.draw_name = this.name + " Gutshot";
                    return true; //return 2 - hay gutshot
                }
            }
        }
        
        return false;
    }
    
    @Override
    public char getHighCard() {
        return 'X'; //no es necesario saber la carta alta en esta jugada
    }
    
}
