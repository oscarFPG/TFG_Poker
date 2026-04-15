package com.ucm.server.gameobjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;

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
     * Player's private hand cards.
     */
    protected List<Card> hand = new ArrayList<>();

    /**
     * Community cards on the table.
     */
    protected List<Card> table = new ArrayList<>();

    /**
     * History of actions in the current hand.
     */
    protected List<String> actionHistory = new ArrayList<>();

    /**
     * Current amount of money available.
     */
    protected int money;

    /**
     * Small blind value.
     */
    protected int smallBlind;

    /**
     * Big blind value.
     */
    protected int bigBlind;

    /**
     * Estimated probability of winning the hand.
     */
    protected double _equity;


    /**
     * Default LLM constructor
     */
    public BotLLM() {
        super();
    }

    /**
     * Constructs an LLM-based bot with basic configuration
     * 
     * @param id     player identifier
     * @param name   player name
     * @param money  initial stack
     * @param apiKey API key for the LLM provider
     */
    public BotLLM(int id, String name, int money) {
        super(id, name, money);
    }


    /**
     * Determines the action to take using the LLM.
     * 
     * <p>
     * The method builds a prompt, sends it to the model, extracts the action
     * from the response and sanitizes it to ensure validity.
     * </p>
     * 
     * @param sb     small blind amount
     * @param bb     big blind amount
     * @param maxBet current maximum bet
     * @return sanitized poker action (fold, call, check or raise X)
     */
    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {

        this.smallBlind = sb;
        this.bigBlind = bb;

        String prompt = buildPrompt();
        String response = callModel(prompt);
        String action = extractAction(response);
        String sanitized = sanitize(action);

        return sanitized;
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
    protected String sanitize(String action) {

        action = action.toLowerCase().trim();

        if (action.contains("fold")) return "fold";
        if (action.contains("call")) return "call";
        if (action.contains("check")) return "check";

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

    // ---------------------- PROMPT ----------------------

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
    protected String buildPrompt() {
        return String.format("""
            You are an expert No Limit Texas Hold'em player.

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
                mapRole(_role),
                formatCards(hand),
                table.isEmpty() ? "[]" : formatCards(table),
                money,
                estimatePot(),
                smallBlind / 2.0,
                bigBlind,
                getHistory(),
                _equity
        );
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
            result.add(c.toLetterString()); // TODO : Existe un metodo toString() en Card con el simbolo de la carta

        return "[" + String.join(", ", result) + "]";
    }

    /**
     * Returns the formatted action history.
     * 
     * @return {@link String} with all actions or "None" if empty
     */
    protected String getHistory() {
        return actionHistory.isEmpty() ? "None" : String.join(", ", actionHistory);
    }

    /**
     * Estimates the current pot size based on blinds and action history.
     * 
     * @return estimated pot size
     */
    protected int estimatePot() {
        int pot = smallBlind + bigBlind;

        for (String action : actionHistory) {
            String[] parts = action.split(" ");
            try {
                pot += Double.parseDouble(parts[parts.length - 1]);
            } catch (Exception ignored) {}
        }

        return pot;
    }

    /**
     * Maps a {@link PlayerRole} to a standard poker position string.
     * 
     * @param role player role
     * @return position string (BTN, SB, BB, UTG, etc.)
     */
    protected String mapRole(PlayerRole role) {
        return switch (role) {
            case DEALER -> "BTN";
            case SMALL_BLIND -> "SB";
            case BIG_BLIND -> "BB";
            case UNDER_THE_GUN -> "UTG";
            case HIJACK -> "HJ";
            case LOJACK -> "LJ";
            case CUT_OFF -> "CO";
            default -> "UNKNOWN";
        };
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


    @Override public void notifyPlayerCard(Card c) { hand.add(c); }

    @Override public void notifyTableCard(Card c) { table.add(c); }

    @Override public void notifySmallBlindBet(int amount) { smallBlind = amount; }

    @Override public void notifyBigBlindBet(int amount) { bigBlind = amount; }

    @Override public void notifyPlayerRole(PlayerRole role) { _role = role; }

    /**
     * Registers an action performed by another player.
     * 
     * @param role   player's role
     * @param action action performed
     * @param amount associated amount (if applicable)
     */
    @Override
    public void notifyOtherPlayerAction(IPokerPlayer p) throws IOException {

        String action = p.getLastCommand();
        if(action.equals(GameType.RAISE_ACTION_FULL) || action.equals(GameType.ALL_IN_ACTION_FULL)) {
            actionHistory.add( mapRole(p.getRole()) + " " + action + " " + p.getMoneyOnBet() );
        }
        else {
            actionHistory.add( mapRole(p.getRole()) + " " + action );
        }
    }

    @Override
    public void notifyTotalPot(int total) throws IOException {
        // TODO : Notificar el total del pot para mostrarlo en el prompt
    }

    @Override
    public void notifyPlayerState(IPokerPlayer player, boolean last) throws IOException {
        // TODO : Notifies the state of all players in the game, including the bot itself
        // Update information
    }

    /**
     * Resets the internal state at the end of a hand.
     */
    @Override
    public void notifyHandEnded() {
        hand.clear();
        table.clear();
        actionHistory.clear();
    }

    @Override public void notifyTurnWait() throws IOException {}
    @Override public void notifyTurnPlay() throws IOException {}
    @Override public void notifyRoundEnded() throws IOException {}
    @Override public void notifyGameEnded() throws IOException {}
    @Override public void notifyGameKeeps() throws IOException {}
    @Override public void notifyGameWinner() throws IOException {}
    @Override public void notifyGameLoser() throws IOException {}
    @Override public void notifyHandEndsByFolds() throws IOException {}

    /**
     * Updates the player's equity.
     * 
     * @param equity probability of winning
     */
    @Override public void notifyEquity(double equity) {
        _equity = equity; 
    }

}