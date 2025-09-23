package hja.grupo3.practica3_hja.plays;
import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.misc.CardComparatorNumber;
import java.util.Arrays;

 /*
    Cuando ningún jugador consigue formar una de las jugadas arriba expuestas, gana la mano 
    aquel que tiene la carta más alta. Y en caso de empate, se usa la siguiente carta más alta.
*/

public class High_Card extends Play {
    
    private char high_Card = 'X'; // no se sabe aún
    public High_Card (){
        this.name = "High Card";
    }
  
    @Override
    public boolean checkPlay(Card[] cards) {
        Card c[] = new Card[cards.length];
        CardComparatorNumber customComparator = new CardComparatorNumber();
        for (int i = 0; i < cards.length; i++){
            c[i] = cards[i];
        }
        Arrays.sort(c, customComparator);
        high_Card = c[0].getNumber();
        this.name = this.name + " (" + c[0].getNumber() +c[0].getSuit() + ")";
        return true;
    }

    @Override
    public char[] getHighCard() {
        char[] hc = {high_Card};
        return hc;
    }    
}