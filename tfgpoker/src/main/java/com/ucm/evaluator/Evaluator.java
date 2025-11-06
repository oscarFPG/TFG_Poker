package com.ucm.evaluator;

import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Suit;
import com.ucm.middleclasses.HandInfo;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;


public class Evaluator {

    public enum RANK {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH
    }

    private static int PRIME_NUMBERS[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };

    /**
     * Evitar instanciamiento desde fuera de esta clase
     */
    private Evaluator(){} 
    
    
    private void loadEvaluator()throws IOException{
        
        List<String> lineas = Files.readAllLines(Path.of("../arrays/test.txt"));
        short[] datos = new short[ lineas.size() ];
        
        for(int i = 0; i < lineas.size(); i++){
            datos[i] = Short.parseShort( lineas.get(i).trim() );
        }
        
    }

    public static Player evaluateAllHands(HandInfo[] playerHands, Card[] tableCards){
       
        int encodedPlayerCards[][] = new int[playerHands.length][2];
        int encodedTableCards[] = new int[tableCards.length];
        Player winner = null;

        for(int i = 0; i < playerHands.length; i++){
            encodedPlayerCards[i][0] = encodeCard(playerHands[i].cards()[0]);
            encodedPlayerCards[i][1] = encodeCard(playerHands[i].cards()[1]);
        }
        for(int i = 0; i < tableCards.length; i++){
            encodedTableCards[i] = encodeCard(tableCards[i]);
        }

        Card[] test = {
            new Card(2, Suit.CLUBS), 
            new Card(3, Suit.CLUBS), 
            new Card(4, Suit.CLUBS), 
            new Card(5, Suit.CLUBS), 
            new Card(6, Suit.CLUBS)
        };
        short value = evaluate5hand( test );

        return winner;
    }
    
    static private short evaluate5hand(Card[] cards){
        
        if(cards.length != 5)
            return -1;


        return 0;
    }


    private RANK hand_rank(short val){

        if (val > 6185) return RANK.HIGH_CARD;        // 1277 high card
        if (val > 3325) return RANK.ONE_PAIR;         // 2860 one pair
        if (val > 2467) return RANK.TWO_PAIR;         //  858 two pair
        if (val > 1609) return RANK.THREE_OF_A_KIND;  //  858 three-kind
        if (val > 1599) return RANK.STRAIGHT;         //   10 straights
        if (val > 322)  return RANK.FLUSH;            // 1277 flushes
        if (val > 166)  return RANK.FULL_HOUSE;       //  156 full house
        if (val > 10)   return RANK.FOUR_OF_A_KIND;   //  156 four-kind
        return RANK.STRAIGHT_FLUSH;                   //   10 straight-flushes
    }

    private static int encodeCard(Card c){

        int card = (byte)Evaluator.PRIME_NUMBERS[c.getNumber() - 2];

        card |= ( (byte)c.getNumber() << 8 );
        card |= ( (byte)encodeSuit(c.getSuit()) << 12 );
        card |= ( (byte)encodeRank(c.getNumber()) << 16 );

        return card;
    }

    private static byte encodeSuit(Suit suit){
    
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
    
    private static byte encodeRank(int rank){
        return (byte)(1 << (rank - 2));
    }
    
}