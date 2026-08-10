package com.ucm.server.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.server.exceptions.TurnTimeoutException;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPlayerInfo;


/**
 * Concrete implementation of a poker bot powered by the Llama3 LLM.
 */
public class LlamaPokerLLM extends BotLLM {

    private static final Logger log = LogManager.getLogger(LlamaPokerLLM.class);

    /**
     * The unique identifier for this Llama LLM bot, which is used to distinguish it from other types of bots in the game.
     */
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    /**
     * The unique identifier for this Llama LLM bot, which is used to distinguish it from other types of bots in the game.
     */
    private static final int LLAMA_ID = GameType.BOT_LLAMA;

    /**
     * The name of the bot, which is used for display purposes in the game interface.
     */
    public static final String MODEL_NAME = "llamaPokerBot";


    /**
     * Constructs a new instance of the LlamaPokerLLM bot with a unique identifier and a specified style.
     * @param style the style of the bot, which can influence its decision-making behavior
     */
    public LlamaPokerLLM(BotStyle style) {
        super(LLAMA_ID, style);
    }

    
    @Override
    protected String callModel(String prompt) throws IOException, TurnTimeoutException {
        
        String cleanResponse;
        try {

            // Make HTTP connection
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Add timeout for response
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(Bot.SECONDS_TIMEOUT * 1000);

            // Sanitize prompt
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
            """
            .formatted(MODEL_NAME, safePrompt);

            // Send request via JSON
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    conn.getInputStream()
                )
            );

            // Receive model response
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }   
            conn.disconnect();

            // Extract action from JSON
            String raw = response.toString();
            JSONObject obj = new JSONObject(raw);
            cleanResponse = obj.getString("response");

            // Replace special characters
            cleanResponse = cleanResponse.replace("\\u003c", "<")
                            .replace("\\u003e", ">");
        } 
        catch(SocketTimeoutException e) {
            log.error("Ollama bot {} took too much time to respond! Action made in this case: FOLD", MODEL_NAME);
            throw new TurnTimeoutException();
        }

        return cleanResponse;
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

        strBuilder.append("You are a specialist in playing ");
        strBuilder.append(Player.CURRENT_PLAYERS);
        strBuilder.append("-handed Poker No Limit Texas Holdem. ");
        strBuilder.append("The following will be a game scenario and you need to make the optimal decision.\n\n");

        strBuilder.append("Here is a game summary:\n\n");
        strBuilder.append("The small blind is ").append(_smallBlind).append(" chips.\n");
        strBuilder.append("The big blind is ").append(_bigBlind).append(" chips.\n");
        strBuilder.append("Everyone started with ").append(Player.getInitialMoney()).append(" chips.\n");

        //strBuilder.append("The player positions involved in this game are UTG, HJ, CO, BTN, SB, BB.\n");
        try {
            String myCards = formatCardsVerbose( List.of(player.getPlayerCards()) );
            strBuilder.append("In this hand, your position is ")
                .append(mapRole( player.getRole() ))
                .append(", and your holding is ")
                .append(myCards)
                .append(".\n");
        }
        catch (Exception e) {
            log.error("Cause: {}", e.getMessage());
        }
        

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

    /**
     * Formats a list of cards into a verbose string representation, describing each card's value and suit in a human-readable format.
     * The output is structured to clearly indicate the cards held by the player or present on the
     * @param cards the list of {@link Card} objects to be formatted
     * @return a {@link String} representing the verbose description of the cards
     */
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

    /**
     * Formats the cards on the table for a specific street (flop, turn, river) into a string representation.
     * @param start the starting index of the cards on the table for the street
     * @param end the ending index (exclusive) of the cards on the table for the street
     * @return a {@link String} representing the formatted cards for the specified street
     */
    private String formatStreet(int start, int end) {
        List<String> parts = new ArrayList<>();

        for (int i = start; i < end; i++) {
            parts.add(formatCardsVerbose(List.of(table.get(i)))
                    .replace("[", "")
                    .replace("]", ""));
        }

        return String.join(", ", parts);
    }

    /**
     * Retrieves the history of actions taken before the flop.
     * @return a {@link String} representing the preflop action history
     */
    private String getPreflopHistory() {
        return actionHistory.isEmpty() ? "None" : String.join(", ", actionHistory);
    }

    /**
     * Retrieves the history of actions taken after the flop, including the turn and river.
     * If there are no actions recorded, it returns "None".
     * @return a {@link String} representing the postflop action history
     */
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

    @Override
    public String getPlayerModel() {
        return "Llama3-8B";
    }

}