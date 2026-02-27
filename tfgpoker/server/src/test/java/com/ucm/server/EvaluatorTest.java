package com.ucm.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Suit;

import java.io.IOException;

public class EvaluatorTest { // TODO Hacer test para chequear datos que deberían dar falso

    private Card[] tableCards2136;
    private Card[] tableCards1;
    private Card[] tableCards2;
    private Card[] tableCards10;
    private Card[] tableCards14;
    private Card[] tableCards26;
    private Card[] tableCards42;
    private Card[] tableCards253;
    private Card[] tableCards1565;
    private Card[] tableCards1608;
    private Card[] tableCards2461;
    private Card[] tableCards2934;
    private Card[] tableCards5406;
    private Card[] tableCards7459;

    @BeforeEach
    public void setUp() throws IOException {

        Evaluator.getInstance();

        // Cartas 7 7 4 2
        tableCards2136 = new Card[5];

        tableCards2136[0] = new Card(7, Suit.CLUBS);
        tableCards2136[1] = new Card(7, Suit.DIAMONDS);
        tableCards2136[2] = new Card(7, Suit.HEARTS);
        tableCards2136[3] = new Card(4, Suit.SPADES);
        tableCards2136[4] = new Card(2, Suit.CLUBS);

        // Cartas A K Q J T Royal Flush
        tableCards1 = new Card[5];

        tableCards1[0] = new Card(14, Suit.CLUBS);
        tableCards1[1] = new Card(13, Suit.CLUBS);
        tableCards1[2] = new Card(12, Suit.CLUBS);
        tableCards1[3] = new Card(11, Suit.CLUBS);
        tableCards1[4] = new Card(10, Suit.CLUBS);

        // Cartas K Q J T 9 King-High Straight Flush
        tableCards2 = new Card[5];

        tableCards2[0] = new Card(13, Suit.HEARTS);
        tableCards2[1] = new Card(12, Suit.HEARTS);
        tableCards2[2] = new Card(11, Suit.HEARTS);
        tableCards2[3] = new Card(10, Suit.HEARTS);
        tableCards2[4] = new Card(9, Suit.HEARTS);

        // Cartas 5 4 3 2 A Five-High Straight Flush
        tableCards10 = new Card[5];

        tableCards10[0] = new Card(5, Suit.HEARTS);
        tableCards10[1] = new Card(4, Suit.HEARTS);
        tableCards10[2] = new Card(3, Suit.HEARTS);
        tableCards10[3] = new Card(2, Suit.HEARTS);
        tableCards10[4] = new Card(14, Suit.HEARTS);

        // Cartas A A A A T Four Aces
        tableCards14 = new Card[5];

        tableCards14[0] = new Card(14, Suit.HEARTS);
        tableCards14[1] = new Card(14, Suit.HEARTS);
        tableCards14[2] = new Card(14, Suit.HEARTS);
        tableCards14[3] = new Card(14, Suit.HEARTS);
        tableCards14[4] = new Card(10, Suit.CLUBS);

        // Cartas K K K K T Four Kings
        tableCards26 = new Card[5];

        tableCards26[0] = new Card(13, Suit.HEARTS);
        tableCards26[1] = new Card(13, Suit.HEARTS);
        tableCards26[2] = new Card(13, Suit.HEARTS);
        tableCards26[3] = new Card(13, Suit.HEARTS);
        tableCards26[4] = new Card(10, Suit.CLUBS);

        // Cartas K K K K T Four Kings
        tableCards42 = new Card[5];

        tableCards42[0] = new Card(12, Suit.CLUBS);
        tableCards42[1] = new Card(12, Suit.CLUBS);
        tableCards42[2] = new Card(12, Suit.CLUBS);
        tableCards42[3] = new Card(12, Suit.CLUBS);
        tableCards42[4] = new Card(6, Suit.HEARTS);

        // Cartas 7 7 7 Q Q Sevens Full over Queens
        tableCards253 = new Card[5];

        tableCards253[0] = new Card(7, Suit.DIAMONDS);
        tableCards253[1] = new Card(7, Suit.DIAMONDS);
        tableCards253[2] = new Card(7, Suit.DIAMONDS);
        tableCards253[3] = new Card(12, Suit.HEARTS);
        tableCards253[4] = new Card(12, Suit.HEARTS);

        // Cartas 9 8 5 3 2 Nine-High Flush
        tableCards1565 = new Card[5];

        tableCards1565[0] = new Card(9, Suit.DIAMONDS);
        tableCards1565[1] = new Card(8, Suit.DIAMONDS);
        tableCards1565[2] = new Card(5, Suit.DIAMONDS);
        tableCards1565[3] = new Card(3, Suit.DIAMONDS);
        tableCards1565[4] = new Card(2, Suit.DIAMONDS);

        // Cartas 6 5 4 3 2 Six-High Straight
        tableCards1608 = new Card[5];

        tableCards1608[0] = new Card(6, Suit.CLUBS);
        tableCards1608[1] = new Card(5, Suit.SPADES);
        tableCards1608[2] = new Card(4, Suit.CLUBS);
        tableCards1608[3] = new Card(3, Suit.DIAMONDS);
        tableCards1608[4] = new Card(2, Suit.DIAMONDS);

        // Cartas 2 2 2 7 3 Three Deuces
        tableCards2461 = new Card[5];

        tableCards2461[0] = new Card(2, Suit.SPADES);
        tableCards2461[1] = new Card(2, Suit.DIAMONDS);
        tableCards2461[2] = new Card(2, Suit.CLUBS);
        tableCards2461[3] = new Card(7, Suit.CLUBS);
        tableCards2461[4] = new Card(3, Suit.HEARTS);

        // Cartas T T 9 9 8 Tens and Nines
        tableCards2934 = new Card[5];

        tableCards2934[0] = new Card(10, Suit.SPADES);
        tableCards2934[1] = new Card(10, Suit.SPADES);
        tableCards2934[2] = new Card(9, Suit.CLUBS);
        tableCards2934[3] = new Card(9, Suit.CLUBS);
        tableCards2934[4] = new Card(8, Suit.HEARTS);

        // Cartas 5 5 Q J T 1P Pair of Fives
        tableCards5406 = new Card[5];

        tableCards5406[0] = new Card(5, Suit.SPADES);
        tableCards5406[1] = new Card(5, Suit.SPADES);
        tableCards5406[2] = new Card(12, Suit.HEARTS);
        tableCards5406[3] = new Card(11, Suit.HEARTS);
        tableCards5406[4] = new Card(10, Suit.HEARTS);

        // Cartas 7 6 5 4 2 HC Seven-High
        tableCards7459 = new Card[5];

        tableCards7459[0] = new Card(7, Suit.HEARTS);
        tableCards7459[1] = new Card(6, Suit.SPADES);
        tableCards7459[2] = new Card(5, Suit.HEARTS);
        tableCards7459[3] = new Card(4, Suit.HEARTS);
        tableCards7459[4] = new Card(2, Suit.SPADES);

    }

    @Test
    public void testEncodeCardNoDevuelveCero() {
        Card carta = new Card(7, Suit.CLUBS);
        int valor = Evaluator.encodeCard(carta);
        assertTrue(valor > 0, "El valor codificado de la carta debe ser mayor que 0");
    }

    // 7 7 7 4 2
    @Test
    public void evaluate5hand2136Test() {
        int c1 = Evaluator.encodeCard(tableCards2136[0]);
        int c2 = Evaluator.encodeCard(tableCards2136[1]);
        int c3 = Evaluator.encodeCard(tableCards2136[2]);
        int c4 = Evaluator.encodeCard(tableCards2136[3]);
        int c5 = Evaluator.encodeCard(tableCards2136[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 2136);

    }

    // A K Q J T Royal Flush
    @Test
    public void evaluate5hand1Test() {
        int c1 = Evaluator.encodeCard(tableCards1[0]);
        int c2 = Evaluator.encodeCard(tableCards1[1]);
        int c3 = Evaluator.encodeCard(tableCards1[2]);
        int c4 = Evaluator.encodeCard(tableCards1[3]);
        int c5 = Evaluator.encodeCard(tableCards1[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 1);

    }

    // K Q J T 9 King-High Straight Flush
    @Test
    public void evaluate5hand2Test() {
        int c1 = Evaluator.encodeCard(tableCards2[0]);
        int c2 = Evaluator.encodeCard(tableCards2[1]);
        int c3 = Evaluator.encodeCard(tableCards2[2]);
        int c4 = Evaluator.encodeCard(tableCards2[3]);
        int c5 = Evaluator.encodeCard(tableCards2[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 2);

    }

    // 5 4 3 2 A Five-High Straight Flush
    @Test
    public void evaluate5hand10Test() {
        int c1 = Evaluator.encodeCard(tableCards10[0]);
        int c2 = Evaluator.encodeCard(tableCards10[1]);
        int c3 = Evaluator.encodeCard(tableCards10[2]);
        int c4 = Evaluator.encodeCard(tableCards10[3]);
        int c5 = Evaluator.encodeCard(tableCards10[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 10); // assertFalse(Evaluator.evaluate5hand(c1, c2,
                                                                       // c3, c4, c5) == 11);

    }

    // A A A A T Four Aces
    @Test
    public void evaluate5hand14Test() {
        int c1 = Evaluator.encodeCard(tableCards14[0]);
        int c2 = Evaluator.encodeCard(tableCards14[1]);
        int c3 = Evaluator.encodeCard(tableCards14[2]);
        int c4 = Evaluator.encodeCard(tableCards14[3]);
        int c5 = Evaluator.encodeCard(tableCards14[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 14);

    }

    // K K K K T Four Kings
    @Test
    public void evaluate5hand26Test() {
        int c1 = Evaluator.encodeCard(tableCards26[0]);
        int c2 = Evaluator.encodeCard(tableCards26[1]);
        int c3 = Evaluator.encodeCard(tableCards26[2]);
        int c4 = Evaluator.encodeCard(tableCards26[3]);
        int c5 = Evaluator.encodeCard(tableCards26[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 26);

    }

    // Q Q Q Q 6 Four Queens
    @Test
    public void evaluate5hand42Test() {
        int c1 = Evaluator.encodeCard(tableCards42[0]);
        int c2 = Evaluator.encodeCard(tableCards42[1]);
        int c3 = Evaluator.encodeCard(tableCards42[2]);
        int c4 = Evaluator.encodeCard(tableCards42[3]);
        int c5 = Evaluator.encodeCard(tableCards42[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 42);

    }

    // 7 7 7 Q Q Sevens Full over Queens
    @Test
    public void evaluate5hand253Test() {
        int c1 = Evaluator.encodeCard(tableCards253[0]);
        int c2 = Evaluator.encodeCard(tableCards253[1]);
        int c3 = Evaluator.encodeCard(tableCards253[2]);
        int c4 = Evaluator.encodeCard(tableCards253[3]);
        int c5 = Evaluator.encodeCard(tableCards253[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 253);

    }

    // 9 8 5 3 2 Nine-High Flush
    @Test
    public void evaluate5hand1565Test() {
        int c1 = Evaluator.encodeCard(tableCards1565[0]);
        int c2 = Evaluator.encodeCard(tableCards1565[1]);
        int c3 = Evaluator.encodeCard(tableCards1565[2]);
        int c4 = Evaluator.encodeCard(tableCards1565[3]);
        int c5 = Evaluator.encodeCard(tableCards1565[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 1565);

    }

    // 6 5 4 3 2 Six-High Straight
    @Test
    public void evaluate5hand1608Test() {
        int c1 = Evaluator.encodeCard(tableCards1608[0]);
        int c2 = Evaluator.encodeCard(tableCards1608[1]);
        int c3 = Evaluator.encodeCard(tableCards1608[2]);
        int c4 = Evaluator.encodeCard(tableCards1608[3]);
        int c5 = Evaluator.encodeCard(tableCards1608[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 1608);

    }

    // 2 2 2 7 3 Six-High Straight
    @Test
    public void evaluate5hand2461Test() {
        int c1 = Evaluator.encodeCard(tableCards2461[0]);
        int c2 = Evaluator.encodeCard(tableCards2461[1]);
        int c3 = Evaluator.encodeCard(tableCards2461[2]);
        int c4 = Evaluator.encodeCard(tableCards2461[3]);
        int c5 = Evaluator.encodeCard(tableCards2461[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 2461);

    }

    // T T 9 9 8 Tens and Nines
    @Test
    public void evaluate5hand2934Test() {
        int c1 = Evaluator.encodeCard(tableCards2934[0]);
        int c2 = Evaluator.encodeCard(tableCards2934[1]);
        int c3 = Evaluator.encodeCard(tableCards2934[2]);
        int c4 = Evaluator.encodeCard(tableCards2934[3]);
        int c5 = Evaluator.encodeCard(tableCards2934[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 2934);

    }

    // 5 5 Q J T 1P Pair of Fives
    @Test
    public void evaluate5hand5406Test() {
        int c1 = Evaluator.encodeCard(tableCards5406[0]);
        int c2 = Evaluator.encodeCard(tableCards5406[1]);
        int c3 = Evaluator.encodeCard(tableCards5406[2]);
        int c4 = Evaluator.encodeCard(tableCards5406[3]);
        int c5 = Evaluator.encodeCard(tableCards5406[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 5406);

    }

    // 7 6 5 4 2 HC Seven-High
    @Test
    public void evaluate5hand7459Test() {
        int c1 = Evaluator.encodeCard(tableCards7459[0]);
        int c2 = Evaluator.encodeCard(tableCards7459[1]);
        int c3 = Evaluator.encodeCard(tableCards7459[2]);
        int c4 = Evaluator.encodeCard(tableCards7459[3]);
        int c5 = Evaluator.encodeCard(tableCards7459[4]);

        assertEquals(Evaluator.evaluate5hand(c1, c2, c3, c4, c5), 7459);

    }

}
