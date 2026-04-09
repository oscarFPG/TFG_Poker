package com.ucm.common;

import java.io.IOException;
import java.io.InputStream;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Suit;

public class PokerGame {
 
    private PokerGame() {}


    public static Card receiveCard(InputStream in) throws IOException {
        
		int valueCode = SocketUtils.receiveInt(in);
		int suitCode = SocketUtils.receiveInt(in);

        Card c = new Card(
            Card.getCardValueFromCode(valueCode), 
            Suit.getSuitFromCode(suitCode)
        );
        
		return c;
    }

}