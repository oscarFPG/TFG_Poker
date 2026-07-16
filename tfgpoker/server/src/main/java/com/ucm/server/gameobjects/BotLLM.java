package com.ucm.server.gameobjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPlayerInfo;

/**
 * Abstract class that represents a poker bot powered by a Large Language Model (LLM).
 * 
 * <p>
 * This class extends {@link Bot} and introduces the concept of an external
 * LLM provider through an API key or a local model one.
 * </p>
 * 
 * <p>
 * It defines the required interface for bots that rely on language models
 * to make decisions, including:
 * </p>
 * <ul>
 * <li>Game action decision making</li>
 * <li>Game state notifications</li>
 * <li>Tracking opponent actions</li>
 * </ul>
 * 
 * <p>
 * Concrete implementations (such as online or local LLM bots) must define
 * how decisions are generated and how game events are processed.
 * </p>
 * 
 * @see BotLLMOnline
 */
public abstract class BotLLM extends Bot {

    /**
     * Community cards on the table.
     */
    protected List<Card> table;

    /**
     * History of actions in the current hand.
     */
    protected List<String> actionHistory;

    /**
     * Small blind value.
     */
    protected int _smallBlind;

    /**
     * Big blind value.
     */
    protected int _bigBlind;

    /**
     * Max bet in the current hand
     */
    protected int _maxBet;

    /**
     * Total pot in the table
     */
    protected int _totalPot;

    /**
     * Estimated probability of winning the hand.
     */
    protected double _equity;


        /**
     * Playing style used by this bot.
     * By default, bots use a balanced playing style.
     */
    protected BotStyle _style = BotStyle.DEFAULT;

    protected IPlayerInfo _player;

    public BotStyle getStyle() {
        return _style;
    }

    public void setPlayingStyle(BotStyle style) {
        _style = (style == null) ? BotStyle.DEFAULT : style;
    }

    /**
     * Default constructor.
     * 
     * @param botID unique identifier for the bot
     */
    public BotLLM(int botID) {
        super(botID);
    }



    /**
     * Calls the external LLM with the given prompt.
     * 
     * <p>
     * This method must be implemented by subclasses to define how the request
     * is sent to the model and how the raw response is retrieved.
     * </p>
     * 
     * @param prompt input prompt describing the game state
     * @return raw response from the model
     */
    protected abstract String callModel(String prompt);


    /**
     * Builds the prompt sent to the LLM.
     * 
     * <p>
     * The prompt contains a structured description of the current game state
     * and strict instructions to ensure consistent output formatting.
     * </p>
     * 
     * @return {@link String} prompt ready to be sent to the model
     */
    protected String buildPrompt(int sb, int bb, int maxBet, IPlayerInfo player) {
        return String.format("""
            You are an expert No Limit Texas Hold'em player.

           Play optimally according to your assigned playing style.

            %s

            This is a 9-handed table.
            Only the players mentioned in the action history are still in the hand.

            The table positions are: UTG (early), HJ (hijack), CO (late), BTN (dealer), SB, BB.

            Your position relative to the button is %s.

            GAME STATE:

            Your hand: %s
            Board: %s
            Stack: %d
            Pot: %d
            Blinds: %.1f/%d

            Action history:
            %s

            Your estimated equity is %.2f.

            It is your turn to act.

            IMPORTANT:
            - Do NOT explain your decision
            - Respond ONLY with one action using EXACTLY one of the following formats:

            <action>fold</action>
            <action>call</action>
            <action>check</action>
            <action>raise AMOUNT</action>
            """,
                mapRole( player.getRole() ),
                formatCards( List.of(player.getPlayerCards()) ),
                table.isEmpty() ? "[]" : formatCards(table),
                player.getMoneyOffBet(),
                _totalPot,
                _smallBlind / 2.0,
                _bigBlind,
                getHistory(),
                _equity
        );
    }

    /**
     * Extracts the action from the LLM response using XML-like tags.
     * 
     * @param text raw response from the model
     * @return extracted action or "fold" if not found
     */
    protected String extractAction(String text) {
        Pattern p = Pattern.compile("<action>(.*?)</action>", Pattern.DOTALL);
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1).trim() : "fold";
    }

    /**
     * Sanitizes the extracted action to ensure it is valid.
     * 
     * <p>
     * This method normalizes outputs, fixes malformed responses and applies
     * fallback strategies when necessary.
     * </p>
     * 
     * @param action raw extracted action
     * @return valid poker action
     */
    protected String sanitize(String action, IPlayerInfo player) {

        action = action.toLowerCase().trim();
        if (action.contains("fold")) return "fold";
        if (action.contains("call")) return "call";
        if (action.contains("check")) return "check";
        if (action.contains("all-in")) return "all-in";


        action = action.replace("bet", "raise");
        Pattern p = Pattern.compile("raise\\s+(\\d+(\\.\\d+)?)");
        Matcher m = p.matcher(action);

        if (m.find()) {
            return "raise " + m.group(1);
        }

        if (action.contains("raise")) {
            return "call";
        }

        return "fold";
    }


    /**
     * Formats a list of cards into a string representation.
     * 
     * @param cards list of {@link Card}
     * @return formatted string (e.g., [Ah, Kd])
     */
    protected String formatCards(List<Card> cards) {

        List<String> result = new ArrayList<>();
        for (Card c : cards)
            result.add( c.toLetterString() );

        return "[" + String.join(", ", result) + "]";
    }


    /**
     * Determines the action to take using the LLM.
     * 
     * <p>
     * THis method performs the next actions in order:
     * 1 - Buids the full prompt with all the info necessary: previous player actions, total pot, own cards, equity, etc...
     * 2 - Calls the model to send the prompt
     * 3 - Extract the action that the model is trying to perform
     * 4 - Sanitize the output if necessary to return the command in the expected format(e.g: "call", "all-in", "raise <amount>", etc...)
     * 5 - Returns the final action
     * </p>
     * 
     * @param sb     small blind amount
     * @param bb     big blind amount
     * @param maxBet current maximum bet
     * @return sanitized poker action (fold, call, check or raise X)
     */
    @Override
    public String notifyMakePlay(int sb, int bb, int maxBet, IPlayerInfo player) throws IOException {
        
        _smallBlind = sb;
        _bigBlind = bb;
        _maxBet = maxBet;

        String prompt = buildPrompt(sb, bb, maxBet, player);
        String response = callModel(prompt);
        String action = extractAction(response);
        String sanitized = sanitize(action, player);

        return sanitized;
    }
    
}