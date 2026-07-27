package com.ucm.server.players;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ucm.common.BotStyle;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.gameobjects.Player;



public class GeminiTest {
 
    
    @Test
    public void testApi() {

        try {

            GeminiLLM geminiLLM = new GeminiLLM(BotStyle.DEFAULT);
            Player player = new Player(0, "Gemini", 1000, geminiLLM);

            player.receiveCard( new Card(2, Suit.CLUBS));
            player.receiveCard( new Card(4, Suit.CLUBS));
            player.receiveRole(PlayerRole.DEALER);
            String response = player.makePlay(1, 2, 2);

            System.out.printf("Gemini response: %s\n", response);
            
            assertEquals(true, true);
        }
        catch(Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
            assertEquals(true, false);
        }   

    }

}