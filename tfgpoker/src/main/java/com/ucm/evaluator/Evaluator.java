package com.ucm.evaluator;

import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Suit;


public class Evaluator {

    private static int PRIME_NUMBERS[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };

    private static Evaluator _eval;

    /**
     * Evitar instanciamiento desde fuera de esta clase
     */
    private Evaluator(){} 
    
    
    /**
     * Asegurarse de que esta clase es un Singleton: 
     * Esta clase utilizará una gran cantidad de memoria y debe ser accesible por cualquier partida sin necesidad de instanciamiento
     */
    static public Evaluator getInstance(){
        
        if(_eval == null){
            _eval = new Evaluator();
        }
        
        return _eval;
    }

    
    public void evaluateAllHands(Card[] cards, Player[] players){
       
        
        
    }
    
    private byte encodeSuit(Suit suit){
    
        byte s = 0; 
        switch(suit){
        case Suit.SPADES:
            s |= (1 << 0);
            break;
            
        case Suit.HEARTS:
            s |= (1 << 1);
            break;
            
        case Suit.DIAMONDS:
            s |= (1 << 2);
            break;
            
        default:
            s |= (1 << 3);
            break;
        }
        
        return s;
    }
    
    private byte encodeRank(int rank){
        return (byte)(1 << (rank - 2));
    }
    
    private int encodeCard(Card c){

        int card = (byte)Evaluator.PRIME_NUMBERS[c.getNumber() - 2];

        card |= ( (byte)c.getNumber() << 8 );
        card |= ( (byte)encodeSuit(c.getSuit()) << 12 );
        card |= ( (byte)encodeRank(c.getNumber()) << 16 );

        return card;
    }
    
    private void evaluate5hand(){
        
    }


}