package com.ucm.server.players;

import java.io.BufferedReader;
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
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;


public class QwenPokerLLM extends BotLLM {

   
    private List<Card> hand = new ArrayList<>();
    private List<Card> table = new ArrayList<>();
    private List<String> actionHistory = new ArrayList<>();
    private int money;
    private int smallBlind;
    private int bigBlind;
    private PlayerRole role;


    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "qwenPokerBot"; 

    public QwenPokerLLM(int id, int money) {
        super(id, "QwenPoker", money, null);
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
        """.formatted(role, hand, table, money, smallBlind, bigBlind, maxBet);
    }

   
    private String callOllama(String prompt) {
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

        } catch (Exception e) {
            e.printStackTrace();
            return "<action>fold</action>";
        }
    }

   
    private String extractAction(String text) {
        Pattern p = Pattern.compile("<action>(.*?)</action>");
        Matcher m = p.matcher(text);

        if (m.find()) {
            return m.group(1);
        }

        return "fold";
    }

    
    private String sanitize(String action) {
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

        String response = callOllama(prompt);

        String action = extractAction(response);

        return sanitize(action);
    }

    
    @Override
    public void notifyPlayerCard(Card c) {
        hand.add(c);
    }

    @Override
    public void notifyTableCard(Card c) {
        table.add(c);
    }

    @Override
    public void notifyMoneyAmount(int amount) {
        money = amount;
    }

    @Override
    public void notifySmallBlindBet(int amount) {
        smallBlind = amount;
    }

    @Override
    public void notifyBigBlindBet(int amount) {
        bigBlind = amount;
    }

    @Override
    public void notifyPlayerRole(PlayerRole role) {
        this.role = role;
    }

   
    @Override
    public void notifyHandEnded() {
        hand.clear();
        table.clear();
        actionHistory.clear();
    }

   
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
        String entry;

        if (action.equals("fold") || action.equals("call") || action.equals("check")) {
            entry = role + " " + action;
        } else if (action.equals("raise") || action.equals("bet")) {
            entry = role + " " + action + " " + amount;
        } else if (action.equals("all-in")) {
            entry = role + " all-in " + amount;
        } else {
            entry = role + " " + action;
        }

        actionHistory.add(entry);
    }

    private String getActionHistory() {
        if (actionHistory.isEmpty()) return "None";

        return String.join(", ", actionHistory);
    }

    @Override public void notifyTurnWait() {}
    @Override public void notifyTurnPlay() {}
    @Override public void notifyRoundEnded() {}
    @Override public void notifyGameEnded() {}
    @Override public void notifyGameKeeps() {}
    @Override public void notifyHandWinner() {}
    @Override public void notifyHandLoser() {}
    @Override public void notifyGameWinner() {}
    @Override public void notifyGameLoser() {}
    @Override public void notifyHandEndsByFolds() {}
    @Override public void notifyOtherPlayerAction(IPokerPlayer p) {}

    @Override
    public void notifyEquity(double equity) {
       
    }

    
}