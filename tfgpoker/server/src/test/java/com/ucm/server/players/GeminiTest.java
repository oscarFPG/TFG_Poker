package com.ucm.server.players;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.ServerMain;



public class GeminiTest {
 
    
    //@Test
    public void testApi() {

        try {
            InputStream input = ServerMain.class
                                .getClassLoader()
                                .getResourceAsStream("credentials.json");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> json = mapper.readValue(input, Map.class);
            String key = json.get("GEMINI_API_KEY");
            
            GeminiLLM geminiLLM = new GeminiLLM(0, 1000, key);
            geminiLLM.receiveRole(PlayerRole.DEALER);
            String response = geminiLLM.actionMakePlay(0, 0, 0);

            System.out.printf("Gemini response: %s\n", response);
            assertEquals(true, true);
        }
        catch(Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
            assertEquals(true, false);
        }   

    }

}