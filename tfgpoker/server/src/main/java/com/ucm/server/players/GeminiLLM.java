package com.ucm.server.players;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.BotLLM;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class GeminiLLM extends BotLLM {

    public enum PlayStyle {
        TIGHT,
        PASSIVE,
        AGGRESSIVE,
        MANIAC
    }

    private final PlayStyle style;

    private List<Card> hand = new ArrayList<>();
    private List<Card> table = new ArrayList<>();
    private List<String> actionHistory = new ArrayList<>();

    private int money;
    private int smallBlind;
    private int bigBlind;
    private PlayerRole role;
    private double equity;

    private ChatModel gemini;

    public GeminiLLM(int id, int money, String apiKey, PlayStyle style) {
        super(id, "GeminiLLM", money, apiKey);
        this.money = money;
        this.style = style;

        gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(_apiKey)
                .modelName("gemini-2.5-flash")
                .build();
    }

    // ---------------------- MAIN ----------------------

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {

        this.smallBlind = sb;
        this.bigBlind = bb;

        
        String prompt = buildPrompt();

        String response = gemini.chat(
                "You must strictly follow the output format.\n\n" + prompt
        );

        String action = extractAction(response);
        action = sanitize(action);

       
        if (equity < 0.05 && !action.equals("check")) {
            return "fold";
        }

     
        if (action.startsWith("raise")) {
            return "raise " + calculateSizing(maxBet);
        }

        return action;
    }

    // ---------------------- STYLE ----------------------

    private String getStyleDescription() {
        return switch (style) {

            case TIGHT -> """
                - Play very selectively
                - Avoid marginal hands
                - Fold frequently
                - Only raise with strong hands
            """;

            case PASSIVE -> """
                - Prefer calling and checking
                - Avoid aggressive actions
                - Only raise with very strong hands
            """;

            case AGGRESSIVE -> """
                - Apply pressure frequently
                - Raise with medium and strong hands
                - Bluff occasionally
                - Avoid passive play
            """;

            case MANIAC -> """
                - Play extremely aggressively
                - Bluff frequently
                - Raise often even with weak hands
                - Apply maximum pressure
            """;
        };
    }

    // ---------------------- PROMPT ----------------------

    private String buildPrompt() {

        return String.format("""
You are an expert No Limit Texas Hold'em player with strong knowledge of GTO and exploitative play.

Play according to this style:

%s

Make the best possible decision.

GAME STATE:

Position: %s
Hand: %s
Board: %s
Stack: %d
Pot: %d
Blinds: %.1f/%d

Action history:
%s

Equity: %.2f

GUIDELINES:
- Use equity as an important factor
- Consider position and opponent actions
- Follow the assigned playstyle strictly

RULES:
- Do NOT explain your decision
- Output ONLY one action

FORMAT:
<action>fold</action>
<action>call</action>
<action>check</action>
<action>raise</action>

Return ONLY the action.
""",
                getStyleDescription(),
                mapRole(role),
                formatCards(hand),
                table.isEmpty() ? "[]" : formatCards(table),
                money,
                estimatePot(),
                smallBlind / 2.0,
                bigBlind,
                getHistory(),
                equity
        );
    }

  
    private double calculateSizing(int maxBet) {

        double pot = estimatePot();

        double size = switch (style) {
            case TIGHT -> pot * 0.5;
            case PASSIVE -> pot * 0.4;
            case AGGRESSIVE -> pot * 0.75;
            case MANIAC -> pot * 1.2;
        };

        return Math.min(size, money);
    }

    // ---------------------- HELPERS ----------------------

    private String formatCards(List<Card> cards) {
        List<String> result = new ArrayList<>();
        for (Card c : cards) result.add(c.toString());
        return "[" + String.join(", ", result) + "]";
    }

    private String getHistory() {
        return actionHistory.isEmpty() ? "None" : String.join(", ", actionHistory);
    }

    private int estimatePot() {
        int pot = smallBlind + bigBlind;

        for (String action : actionHistory) {
            String[] parts = action.split(" ");
            try {
                pot += Double.parseDouble(parts[parts.length - 1]);
            } catch (Exception ignored) {}
        }

        return pot;
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

    // ---------------------- PARSING ----------------------

    private String extractAction(String text) {
        Pattern p = Pattern.compile("<action>(.*?)</action>", Pattern.DOTALL);
        Matcher m = p.matcher(text);

        if (m.find()) return m.group(1).trim();

        return "fold";
    }

    private String sanitize(String action) {
        action = action.toLowerCase().trim();

        if (action.contains("fold")) return "fold";
        if (action.contains("call")) return "call";
        if (action.contains("check")) return "check";
        if (action.contains("raise")) return "raise";

        return "fold";
    }

    // ---------------------- NOTIFY ----------------------

    @Override public void notifyPlayerCard(Card c) { hand.add(c); }
    @Override public void notifyTableCard(Card c) { table.add(c); }
    @Override public void notifyMoneyAmount(int amount) { money = amount; }
    @Override public void notifySmallBlindBet(int amount) { smallBlind = amount; }
    @Override public void notifyBigBlindBet(int amount) { bigBlind = amount; }
    @Override public void notifyPlayerRole(PlayerRole role) { this.role = role; }

    @Override
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
        actionHistory.add(mapRole(role) + " " + action +
                ((action.equals("raise") || action.equals("all-in")) ? " " + amount : ""));
    }

    @Override
    public void notifyEquity(double equity) {
        this.equity = equity;
    }

    @Override
    public void notifyHandEnded() {
        hand.clear();
        table.clear();
        actionHistory.clear();
    }

    // ---------------------- UNUSED ----------------------

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

    @Override
    public String getDescription() {
        return "Gemini Poker LLM (" + style + ")";
    }
}