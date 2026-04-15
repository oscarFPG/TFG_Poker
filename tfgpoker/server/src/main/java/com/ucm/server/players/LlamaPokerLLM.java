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

import org.json.JSONObject;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.commands.Command;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;



public class LlamaPokerLLM extends BotLLM {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llamaPokerBot";

    public LlamaPokerLLM(int id, int money) {
        super(id, "LlamaPoker", money);
        this.money = money;
    }

    // -------------------------------------METODOS COMUNES PARA TODOS LOS BOTS------------------------------------------------

    @Override
    public String getDescription() {
        return "Llama Poker LLM (Ollama)";
    }

    @Override
    public String notifyMakePlay(int sb, int bb, int maxBet) {
        this.smallBlind = sb;
        this.bigBlind = bb;
        String prompt = buildPrompt(maxBet);
        String response = callModel(prompt);
        System.out.println("PROMPT:\n" + prompt);
        String action = extractAction(response);
        return sanitize(action);
    }  

    // ---------------------------------------------METODOS PERSOLANIZADOS PARA ESTE BOT------------------------------------------------
   

    // VA MAS LENTO PERO ACIERTA MAS
    protected String buildPrompt(int maxBet) {

        StringBuilder sb = new StringBuilder();

        sb.append("You are a specialist in playing 9-handed No Limit Texas Holdem. ");
        sb.append("The following will be a game scenario and you need to make the optimal decision.\n\n");

        sb.append("Here is a game summary:\n\n");

        sb.append("The small blind is ").append(smallBlind / 2.0)
          .append(" chips and the big blind is ").append(bigBlind)
          .append(" chips. Everyone started with 100 chips.\n");

        sb.append("The player positions involved in this game are UTG, HJ, CO, BTN, SB, BB.\n");

        sb.append("In this hand, your position is ")
          .append(mapRole(_role))
          .append(", and your holding is ")
          .append(formatCardsVerbose(hand))
          .append(".\n");

        sb.append("Before the flop, ")
          .append(getPreflopHistory())
          .append(". Assume that all other players that is not mentioned folded.\n");

        if (table.size() >= 3) {
            sb.append("The flop comes ")
              .append(formatStreet(0, 3))
              .append(", then ")
              .append(getPostflopHistory())
              .append(".\n");
        }

        if (table.size() >= 4) {
            sb.append("The turn comes ")
              .append(formatStreet(3, 4))
              .append(", then ")
              .append(getPostflopHistory())
              .append(".\n");
        }

        if (table.size() == 5) {
            sb.append("The river comes ")
              .append(formatStreet(4, 5))
              .append(", then ")
              .append(getPostflopHistory())
              .append(".\n");
        }

        sb.append("\nNow it is your turn to make a move.\n");

        sb.append("To remind you, the current pot size is ")
          .append(estimatePot())
          .append(" chips, and your holding is ")
          .append(formatCardsVerbose(hand))
          .append(".\n\n");

        sb.append("Decide on an action based on the strength of your hand on this board, your position, and actions before you. ");
        sb.append("Do not explain your answer.\n");
        sb.append("Write your optimal action between this tags <action>answer</action>:");

        return sb.toString();
    }



    // VA MAS RAPIDO PERO ACIERTA CON MENOS FRECUENCIA 
   private String reducedPrompt(int maxBet) {

        StringBuilder sb = new StringBuilder();

        sb.append("You are a specialist in playing 9-handed No Limit Texas Holdem.\n\n");

        sb.append("Here is a game summary:\n\n");

        sb.append("Position: ").append(mapRole(_role)).append("\n");
        sb.append("Hand: ").append(formatCardsVerbose(hand)).append("\n");

        if (!table.isEmpty()) {
            sb.append("Board: ").append(formatCardsVerbose(table)).append("\n");
        }

        sb.append("Stack: ").append(money).append("\n");
        sb.append("Pot: ").append(estimatePot()).append("\n");
        sb.append("Blinds: ").append(smallBlind / 2.0).append("/").append(bigBlind).append("\n\n");

        sb.append("Action history: ").append(getPreflopHistory()).append(".\n");
        sb.append("Assume that all other players that is not mentioned folded.\n\n");

        sb.append("It is your turn.\n\n");

        sb.append("Decide on an action based on the strength of your hand on this board, your position, and actions before you. ");
        sb.append("Do not explain your answer.\n");
        sb.append("Write your optimal action between this tags <action>answer</action>:");

        return sb.toString();
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
    protected String extractAction(String text) {
        //System.out.println("TEXT BEFORE FILTER: " + text);
        Pattern p = Pattern.compile("<action>(.*?)</action>", Pattern.DOTALL);
        Matcher m = p.matcher(text);

        if (m.find()) {
            return m.group(1).trim();
        }

        return "fold";
    }

    @Override
    protected String sanitize(String action) {

        action = action.toLowerCase().trim();

        if (action.contains("fold")) return "fold";
        if (action.contains("call")) return "call";
        if (action.contains("check")) return "check";
        if (action.contains("all-in")) return "all-in";

        
        action = action.replace("bet", "raise");

        Pattern p = Pattern.compile("^raise\\s+(\\d+(\\.\\d+)?)$");
        Matcher m = p.matcher(action);

       
        if (m.find()) {
            return "raise " + m.group(1);
        }


      
        if (action.startsWith("raise")) {
            return "call";
        }

        return "fold";
    }


	@Override
	public Bot create(int ID, int initialMoney) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'create'");
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
            //System.out.println("RAW JSON:\n" + raw); 

            JSONObject obj = new JSONObject(raw);
            String clean = obj.getString("response");

            
            clean = clean.replace("\\u003c", "<")
                            .replace("\\u003e", ">");

            return clean;

        } 
        catch (Exception e) {
            e.printStackTrace();
            return "<action>fold</action>";
        }
    }

}