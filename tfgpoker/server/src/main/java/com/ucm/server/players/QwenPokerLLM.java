package com.ucm.server.players;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.interfaces.IPlayerInfo;


public class QwenPokerLLM extends BotLLM {

   
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "qwenPokerBot"; 
    private static final int QWEN_ID = 123432;



    public QwenPokerLLM() {
        super(QWEN_ID);
    }


    @Override
    protected String buildPrompt(int sb, int bb, int maxBet) {

        return """

        Game state:
        - Role: %s
        - Hand: %s
        - Table: %s
        - Money: %d
        - Small blind: %d
        - Big blind: %d
        - Current max bet: %d

        Respond EXACTLY in this format:
        <think>your reasoning</think>
        <action>fold/call/raise X/all-in</action>
        """
        .formatted(
            mapRole( _player.getRole() ), 
            List.of(_player.getPlayerCards()), 
            table, 
            _player.getMoneyOffBet(), 
            sb, 
            bb, 
            maxBet
        );
    }

    @Override
    protected String callModel(String prompt) {

        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = """
            {
              "model": "%s",
              "prompt": "%s",
              "stream": false
            }
            """.formatted(MODEL_NAME, prompt.replace("\"", "\\\""));

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();
            return response.toString();

        } 
        catch (Exception e) {
            e.printStackTrace();
            return "<action>fold</action>";
        }
    }

    @Override
    protected String sanitize(String action) {

        action = action.toLowerCase();

        if (action.contains("fold")) return "fold";
        if (action.contains("call")) return "call";
        if (action.contains("all-in")) return "all-in";

        if (action.contains("raise")) {
            return action;
        }

        return "fold";
    }


    @Override
    public Bot create() {
        return new QwenPokerLLM();
    }

    @Override
    public String getDescription() {
        return "Qwen Poker LLM running locally with Ollama";
    }

    @Override
    public String getFullDescription() {
        return "Qwen Poker LLM is a poker-playing bot that uses the Qwen language model, running locally with Ollama.";
    }

    
}