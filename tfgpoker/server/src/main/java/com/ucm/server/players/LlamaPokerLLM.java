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

import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

public class LlamaPokerLLM extends BotLLM {

    private List<Card> hand = new ArrayList<>();
    private List<Card> table = new ArrayList<>();
    private List<String> actionHistory = new ArrayList<>();

    private int money;
    private int smallBlind;
    private int bigBlind;
    private PlayerRole role;

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llamaPokerBot";

    public LlamaPokerLLM(int id, int money) {
        super(id, "LlamaPoker", money, null);
        this.money = money;
    }

    @Override
    public String getDescription() {
        return "Llama Poker LLM (Ollama)";
    }

    private String buildPrompt(int maxBet) {
        return """
        You are a specialist in playing 6-handed No Limit Texas Holdem. The following will be a game scenario and you need to make the optimal decision.

        Here is a game summary:

        Position: %s
        Hand: %s
        Board: %s
        Stack: %d
        Pot: %d
        Blinds: %d/%d

        Action history: %s.
        Assume that all other players that is not mentioned folded.

        It is your turn.

        Decide on an action based on the strength of your hand on this board, your position, and actions before you. Do not explain your answer.
        Write your optimal action between this tags <action>answer</action>:
        """.formatted(
                mapRole(role),
                hand,
                table,
                money,
                estimatePot(),
                smallBlind,
                bigBlind,
                getActionHistory()
        );
    }

    private String mapRole(PlayerRole role) {
    return switch (role) {
        case DEALER -> "BTN";
        case SMALL_BLIND -> "SB";
        case BIG_BLIND -> "BB";
        case UNDER_THE_GUN -> "UTG";
        case MIDDLE_POSITION -> "HJ";  
        case CUT_OFF -> "CO";
        default -> "UNKNOWN";
    };
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
              "stream": false,
              "options": {
                "temperature": 0,
                "top_p": 0.1,
                "num_predict": 30
              }
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

        Pattern p1 = Pattern.compile("<action>(.*?)</action>", Pattern.DOTALL);
        Matcher m1 = p1.matcher(text);
        if (m1.find()) return m1.group(1).trim();

        Pattern p2 = Pattern.compile("<(fold|call|all-in|raise\\s*\\d+\\.?\\d*)/?\\s*>", Pattern.CASE_INSENSITIVE);
        Matcher m2 = p2.matcher(text);
        if (m2.find()) return m2.group(1).trim();

        String lower = text.toLowerCase();

        if (lower.contains("fold")) return "fold";
        if (lower.contains("call")) return "call";
        if (lower.contains("all-in")) return "all-in";
        if (lower.contains("raise")) return "raise";

        return "fold";
    }

    private String sanitize(String action) {
        action = action.toLowerCase().trim();

        if (action.contains("fold")) return "fold";
        if (action.contains("call")) return "call";
        if (action.contains("all-in")) return "all-in";

        if (action.startsWith("raise")) return action;

        return "fold";
    }

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {

        this.smallBlind = sb;
        this.bigBlind = bb;

        String prompt = buildPrompt(maxBet);
        String response = callOllama(prompt);

        System.out.println("PROMPT:\n" + prompt);
        System.out.println("RAW RESPONSE:\n" + response);

        String action = extractAction(response);

        return sanitize(action);
    }

    // ---------------- STATE ----------------

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
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
        String entry;

        if (action.equals("fold") || action.equals("call") || action.equals("check")) {
            entry = mapRole(role) + " " + action;
        } else if (action.equals("raise") || action.equals("bet")) {
            entry = mapRole(role) + " " + action + " " + amount;
        } else if (action.equals("all-in")) {
            entry = mapRole(role) + " all-in " + amount;
        } else {
            entry = mapRole(role) + " " + action;
        }

        actionHistory.add(entry);
    }

    private String getActionHistory() {
        if (actionHistory.isEmpty()) return "None";
        return String.join(", ", actionHistory);
    }

   
    private int estimatePot() {
        int pot = smallBlind + bigBlind;

        for (String action : actionHistory) {
            if (action.contains("raise") || action.contains("bet") || action.contains("all-in")) {
                String[] parts = action.split(" ");
                try {
                    double amount = Double.parseDouble(parts[parts.length - 1]);
                    pot += amount;
                } catch (Exception ignored) {}
            }
        }

        return pot;
    }

    
    @Override
    public void notifyHandEnded() {
        hand.clear();
        table.clear();
        actionHistory.clear();
    }

    // UNUSED
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
}