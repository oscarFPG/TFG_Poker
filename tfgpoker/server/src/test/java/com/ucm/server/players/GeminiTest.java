package com.ucm.server.players;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.Player;



public class GeminiTest {
 
    
    //@Test
    public void testApi() {

        try {

            GeminiLLM geminiLLM = new GeminiLLM();
            Player player = new Player(0, "Gemini", 1000, geminiLLM);

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