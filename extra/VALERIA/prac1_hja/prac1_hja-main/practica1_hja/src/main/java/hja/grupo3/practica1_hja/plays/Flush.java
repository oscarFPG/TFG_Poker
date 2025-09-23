package hja.grupo3.practica1_hja.plays;

import hja.grupo3.practica1_hja.game.Card;

 /*
    El color lo forman cinco cartas no consecutivas del mismo palo. Gana el desempate 
    entre dos colores aquel que tenga la carta más alta.
*/

public class Flush extends Play {

     /*El color lo forman cinco cartas no consecutivas del mismo palo. Gana el desempate 
    entre dos colores aquel que tenga la carta más alta.*/
    public Flush(){
        this.name = "Flush";
        this.draw_name = "Flush";
    }
    @Override
    public boolean checkPlay(Card cards[]) {
        if (0 != numSuits(cards,5)) {
            for (Card c : cards) {c.setSol(true);}
            //this.name += " with (" + cardstoString(cards) + ")";
            return true;
        }
        return false;
    }
    
    @Override
    public boolean checkDraw(Card[] cards) {
        return 0 != numSuits(cards,4);
    }
    
    public char numSuits(Card[] cards, int count) {
        int nH = 0, nD = 0, nC = 0, nS = 0;
        
        for (Card c :  cards) {
            switch (c.getSuit()) {
                case 'h' -> nH++;
                case 'd' -> nD++;
                case 'c' -> nC++;
                case 's' -> nS++;
            }
        }
        
        if (nH >= count)
            return 'h';
        if (nD >= count)
            return 'd';
        if (nC >= count)
            return 'c';
        if (nS >= count)
            return 's';
        
        return 0;
    }

    @Override
    public char getHighCard() {
        return 'X'; //no es necesario saber la carta alta en esta jugada
    }
    
}
