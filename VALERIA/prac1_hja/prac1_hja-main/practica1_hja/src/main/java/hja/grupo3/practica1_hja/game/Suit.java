package hja.grupo3.practica1_hja.game;

/**
 *
 * @author Laura
 */
public class Suit {
    public static final char HEARTS = 'h'; //Corazones
    public static final char DIAMONDS = 'd'; //Diamantes
    public static final char CLUBS = 'c'; //Treboles
    public static final char SPADES = 's'; //Picas
    
    public static char getSuit(int i) {
        switch (i) {
            case 0:
                return HEARTS;
            case 1:
                return DIAMONDS;
            case 2:
                return CLUBS;
            default:
                return SPADES;
        }
    }
}
