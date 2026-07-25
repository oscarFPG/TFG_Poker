package com.ucm.server.players;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.interfaces.IPlayerInfo;


public class LlamaPokerLLM extends BotLLM {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final int LLAMA_ID = GameType.BOT_LLAMA;
    public static final String MODEL_NAME = "llamaPokerBot";

    public LlamaPokerLLM(BotStyle style) {
        super(LLAMA_ID, style);
    }

    
    @Override
    protected String callModel(String prompt) {
        
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String safePrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String json = """
            {
                "model": "%s",
                "system": "You are an expert poker player. Respond ONLY with <action>answer</action>.",
                "prompt": "%s",
                "stream": false,
                "options": {
                "temperature": 0,
                "top_p": 0.1,
                "num_predict": 30
                }
            }
            """.formatted(MODEL_NAME, safePrompt);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }   
            conn.disconnect();

            String raw = response.toString();
            JSONObject obj = new JSONObject(raw);
            String clean = obj.getString("response");

            clean = clean.replace("\\u003c", "<")
                            .replace("\\u003e", ">");

            return clean;
        } 
        catch (Exception e) {
            System.out.printf("ERROR WITH OLLAMA: %s\n", e.getMessage());
            return "<action>fold</action>";
        }
    }

    @Override
    protected String extractAction(String text) {

        Pattern p = Pattern.compile("<action>(.*?)</action>", Pattern.DOTALL);
        Matcher m = p.matcher(text);

        if (m.find()) {
            return m.group(1).trim();
        }

        return "fold";
    }

    @Override
    protected String sanitize(String action, IPlayerInfo player) {

        action = action.toLowerCase().trim();

        if ( action.contains("fold") )
            return GameType.FOLD_ACTION_FULL;
        if ( action.contains("call") )
            return GameType.CALL_ACTION_FULL;
        if ( action.contains("check") )
            return GameType.CHECK_ACTION_FULL;

        
        // Raise action
        action = action.replace("bet", "raise");

        Pattern p = Pattern.compile("^raise\\s+(\\d+(\\.\\d+)?)$");
        Matcher m = p.matcher(action);
        if (m.find()) {
            String targetBet = m.group(1);
            return GameType.RAISE_ACTION_FULL + " " + targetBet;
        }

        return GameType.FOLD_ACTION_FULL;
    }


    // VA MAS LENTO PERO ACIERTA MAS
    @Override
    protected String buildPrompt(int sb, int bb, int maxBet, IPlayerInfo player) {

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("You are a specialist in playing 9-handed No Limit Texas Holdem. ");
        strBuilder.append("The following will be a game scenario and you need to make the optimal decision.\n\n");

        strBuilder.append("Here is a game summary:\n\n");
        strBuilder.append("The small blind is ").append(_smallBlind)
                  .append(" chips and the big blind is ").append(_bigBlind)
                  .append(" chips. Everyone started with 100 chips.\n");

        strBuilder.append("The player positions involved in this game are UTG, HJ, CO, BTN, SB, BB.\n");
        strBuilder.append("In this hand, your position is ")
          .append(mapRole( player.getRole() ))
          .append(", and your holding is ")
          .append(formatCardsVerbose( List.of(player.getPlayerCards()) ))
          .append(".\n");

        strBuilder.append("Before the flop, ")
          .append(getPreflopHistory())
          .append(". Assume that all other players that is not mentioned folded.\n");

        if (table.size() >= 3) {
            strBuilder.append("The flop comes ")
              .append(formatStreet(0, 3))
              .append(", then ")
              .append(getPostflopHistory())
              .append(".\n");
        }

        if (table.size() >= 4) {
            strBuilder.append("The turn comes ")
              .append(formatStreet(3, 4))
              .append(", then ")
              .append(getPostflopHistory())
              .append(".\n");
        }

        if (table.size() == 5) {
            strBuilder.append("The river comes ")
              .append(formatStreet(4, 5))
              .append(", then ")
              .append(getPostflopHistory())
              .append(".\n");
        }

        strBuilder.append("\nNow it is your turn to make a move.\n");

        strBuilder.append("To remind you, the current pot size is ")
          .append(_totalPot)
          .append(" chips, and your holding is ")
          .append( formatCardsVerbose(List.of(player.getPlayerCards())) )
          .append(".\n\n");

        strBuilder.append("Decide on an action based on the strength of your hand on this board, your position, and actions before you. ");
        strBuilder.append("Do not explain your answer.\n");
        strBuilder.append("Write your optimal action between this tags <action>answer</action>:");

        return strBuilder.toString();
    }

    // VA MAS RAPIDO PERO ACIERTA CON MENOS FRECUENCIA 
    private String reducedPrompt(int sb, int bb, int maxBet, IPlayerInfo player) {

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("You are a specialist in playing 9-handed No Limit Texas Holdem.\n\n");

        strBuilder.append("Here is a game summary:\n\n");

        strBuilder.append("Position: ").append(mapRole( player.getRole() )).append("\n");
        strBuilder.append("Hand: ").append(formatCardsVerbose( List.of(player.getPlayerCards()) )).append("\n");

        if (!table.isEmpty()) {
            strBuilder.append("Board: ").append(formatCardsVerbose(table)).append("\n");
        }

        strBuilder.append("Stack: ").append( player.getMoneyOffBet() ).append("\n");
        strBuilder.append("Pot: ").append(_totalPot).append("\n");
        strBuilder.append("Blinds: ").append(sb / 2.0).append("/").append(bb).append("\n\n");

        strBuilder.append("Action history: ").append(getPreflopHistory()).append(".\n");
        strBuilder.append("Assume that all other players that is not mentioned folded.\n\n");

        strBuilder.append("It is your turn.\n\n");

        strBuilder.append("Decide on an action based on the strength of your hand on this board, your position, and actions before you. ");
        strBuilder.append("Do not explain your answer.\n");
        strBuilder.append("Write your optimal action between this tags <action>answer</action>:");

        return strBuilder.toString();
    }


    private String formatCardsVerbose(List<Card> cards) {
        List<String> result = new ArrayList<>();

        for (Card c : cards) {

            String value = switch (c.getNumber()) {
                case 1 -> "Ace";
                case 13 -> "King";
                case 12 -> "Queen";
                case 11 -> "Jack";
                case 10 -> "Ten";
                case 9 -> "Nine";
                case 8 -> "Eight";
                case 7 -> "Seven";
                case 6 -> "Six";
                case 5 -> "Five";
                case 4 -> "Four";
                case 3 -> "Three";
                case 2 -> "Two";
                default -> "?";
            };

            String suit = switch (c.getSuit().getLetra()) {
                case 'h' -> "Heart";
                case 'd' -> "Diamond";
                case 'c' -> "Club";
                case 's' -> "Spade";
                default -> "?";
            };

            result.add(value + " of " + suit);
        }

        return "[" + String.join(" and ", result) + "]";
    }

    private String formatStreet(int start, int end) {
        List<String> parts = new ArrayList<>();

        for (int i = start; i < end; i++) {
            parts.add(formatCardsVerbose(List.of(table.get(i)))
                    .replace("[", "")
                    .replace("]", ""));
        }

        return String.join(", ", parts);
    }

    private String getPreflopHistory() {
        return actionHistory.isEmpty() ? "None" : String.join(", ", actionHistory);
    }

    private String getPostflopHistory() {
        return actionHistory.isEmpty() ? "None" : String.join(", ", actionHistory);
    }


    @Override
	public Bot create(BotStyle style) {
		return new LlamaPokerLLM(style);
	}

    @Override
    public String getDescription() {
        return "Llama Poker LLM (Ollama)";
    }

    @Override
    public String getFullDescription() {
        return "This is the Llama3 local LLM poker bot. It uses the Ollama API to call a local Llama3 model fine-tuned for poker decision making. It provides detailed game context in the prompt and extracts actions from the model's response.";
    }

}