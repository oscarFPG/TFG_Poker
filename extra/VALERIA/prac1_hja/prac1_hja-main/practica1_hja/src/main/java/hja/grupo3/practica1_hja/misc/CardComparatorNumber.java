//Ordena primero por los números y luego por los palos
package hja.grupo3.practica1_hja.misc;

import hja.grupo3.practica1_hja.game.Card;
import java.util.Comparator;

/**
 *
 * @author Laura
 */
public class CardComparatorNumber implements Comparator<Card> {
    private final String numberOrder;
    private final String suitOrder;

    public CardComparatorNumber() {
        this.numberOrder = "23456789TJQKA";
        this.suitOrder = "hdcs";
    }

    @Override
    public int compare(Card card1, Card card2) {
        int numberComparison = numberOrder.indexOf(card2.getNumber()) - numberOrder.indexOf(card1.getNumber());
        
        if (numberComparison == 0) {
            return suitOrder.indexOf(card2.getSuit()) - suitOrder.indexOf(card1.getSuit());
        }
        
        return numberComparison;
    }
    
}
