package com.ucm.server.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.commands.Command;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;


public class QwenPokerLLM extends BotLLM {

   
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "qwenPokerBot"; 


    public QwenPokerLLM(int id, int money) {
        super(id, "QwenPoker", money);
        this.money = money;
    }


    @Override
    public String getDescription() {
        return "Qwen Poker LLM running locally with Ollama";
    }


    private String buildPrompt(int maxBet) {
        return """
        You are an expert Texas Hold'em poker player.

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
        """.formatted(_role, hand, table, money, smallBlind, bigBlind, maxBet);
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
    public String actionMakePlay(int sb, int bb, int maxBet) {

        String prompt = buildPrompt(maxBet);
        String response = callModel(prompt);
        String action = extractAction(response);
        return sanitize(action);
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
    public Bot create(int ID, int initialMoney) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }
    
}