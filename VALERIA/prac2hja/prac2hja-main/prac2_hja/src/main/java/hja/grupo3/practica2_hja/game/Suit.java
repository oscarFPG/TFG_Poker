package hja.grupo3.practica2_hja.game;

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
        return switch (i) {
            case 0 -> HEARTS;
            case 1 -> CLUBS;
            case 2 -> DIAMONDS;
            case 3 -> SPADES;
            default -> '\0';
        };
    }
}
