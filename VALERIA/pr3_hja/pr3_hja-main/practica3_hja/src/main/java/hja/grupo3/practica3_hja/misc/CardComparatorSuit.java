//Ordena primero por los palos y luego por los números
package hja.grupo3.practica3_hja.misc;

import hja.grupo3.practica3_hja.game.Card;
import java.util.Comparator;


/**
 *
 * @author Laura
 */
public class CardComparatorSuit implements Comparator<Card> {
    private final String numberOrder;
    private final String suitOrder;

    public CardComparatorSuit() {
        this.numberOrder = "23456789TJQKA";
        this.suitOrder = "hdcs";
    }

    @Override
    public int compare(Card card1, Card card2) {
        int numberComparison = suitOrder.indexOf(card2.getSuit()) - suitOrder.indexOf(card1.getSuit());
        
        if (numberComparison == 0) {
            return numberOrder.indexOf(card2.getNumber()) - numberOrder.indexOf(card1.getNumber());
        }
        
        return numberComparison;
    }
    
}
