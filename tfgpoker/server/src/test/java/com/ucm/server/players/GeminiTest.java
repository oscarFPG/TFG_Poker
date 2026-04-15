package com.ucm.server.players;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.ServerMain;



public class GeminiTest {
 
    
    //@Test
    public void testApi() {

        try {
            InputStream input = ServerMain.class
                                .getClassLoader()
                                .getResourceAsStream("credentials.json");

            
            GeminiLLM geminiLLM = new GeminiLLM(0, 1000);
            geminiLLM.receiveRole(PlayerRole.DEALER);
            String response = geminiLLM.notifyMakePlay(0, 0, 0);

            System.out.printf("Gemini response: %s\n", response);
            assertEquals(true, true);
        }
        catch(Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
            assertEquals(true, false);
        }   

    }

}