package com.ucm.server.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.logic.Game;
import com.ucm.server.logic.PlayerList;
import com.ucm.server.statistics.Street;


/**
 * Writes PokerStars-style hand histories.
 */
public final class PokerHistory {

        /*--------------------------------------------------
    * CURRENT HISTORY
    *--------------------------------------------------*/

    /**
     * PokerHistory associated with the current execution thread.
     */
    private static final ThreadLocal<PokerHistory> CURRENT = new ThreadLocal<>();

    public static void set(PokerHistory history) {
        CURRENT.set(history);
    }

    public static PokerHistory current() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    private Street currentStreet = Street.PREFLOP;
    private List<Player> players;

    private Player dealer;
    private Player smallBlind;
    private Player bigBlind;

     private final Map<IPlayerActions, Street> foldStreet = new HashMap<>();
    private final Map<IPlayerActions, Integer> winnerPrize = new HashMap<>();

    /**
     * Maps every player to his seat number.
     */
    private Map<Player, Integer> playerSeats;

    private PlayerList playerList;

    private int handNumber;
    private int smallBlindAmount;
    private int bigBlindAmount;

    private static final Logger log = LogManager.getLogger("PokerHistory");

   public PokerHistory(Game game, int handNumber) {
        this.handNumber = handNumber;
        initializePlayers(game);
    }

    private void initializePlayers(Game game) {
        smallBlindAmount = game.getCurrentSmallBlind();
        bigBlindAmount = game.getCurrentBigBlind();

        playerList = game.getPlayerList();
        players = game.getPlayerList().getPlayers();

        dealer = null;
        smallBlind = null;
        bigBlind = null;

        playerSeats = new HashMap<>();

        int seat = 1;

        for (Player player : players) {

            playerSeats.put(player, seat);

            switch (player.getRole()) {

                case DEALER:
                    dealer = player;
                    break;

                case SMALL_BLIND:
                    smallBlind = player;
                    break;

                case BIG_BLIND:
                    bigBlind = player;
                    break;

                default:
                    break;
            }

            seat++;
        }
    }

    /*--------------------------------------------------
     * MATCH
     *--------------------------------------------------*/

   public static void startMatch(String matchId) {
        ThreadContext.put("match", matchId);
    }   

    public static void endMatch() {
        ThreadContext.clearAll();
    }

    /*--------------------------------------------------
     * HAND
     *--------------------------------------------------*/

    public void startHand() {

        foldStreet.clear();
        winnerPrize.clear();
        currentStreet = Street.PREFLOP;

        ThreadContext.put("hand",
                String.format("%04d", handNumber));

        writeHeader();

        blankLine();

        writeSeats();

        blankLine();

        writeBlinds();

        blankLine();

    
        write("*** HOLE CARDS ***");
    }

    public void endHand() {
        ThreadContext.remove("hand");
    }

    /*--------------------------------------------------
     * POKERSTARS SECTIONS
     *--------------------------------------------------*/

   private void writeHeader() {

        int activePlayers = 0;

        for (Player player : players) {
            if (!player.isEliminated())
                activePlayers++;
        }

        write(
                "PokerStars Hand #%06d: Texas Hold'em No Limit (%d/%d)",
                handNumber,
                smallBlindAmount,
                bigBlindAmount);

        if (dealer != null) {

            write(
                    "Table 'Experiment' %d-max Seat #%d is the button",
                    activePlayers,
                    playerSeats.get(dealer));

        } else {

            write(
                    "Table 'Experiment' %d-max",
                    activePlayers);
        }
    }

        private void writeSeats() {

            for (Player player : players) {

                if (player.isEliminated())
                    continue;

                write(
                        "Seat %d: %s (%d in chips)",
                        playerSeats.get(player),
                        player.getPlayerName(),
                        player.getMoneyOffBet());
            }
        }

    private void writeBlinds() {

        if (smallBlind != null) {
                write("%s: posts small blind %d",
                        smallBlind.getPlayerName(),
                        smallBlindAmount);
        }

        if (bigBlind != null) {
            write("%s: posts big blind %d",
                    bigBlind.getPlayerName(),
                    bigBlindAmount);
        }

    }

   /*--------------------------------------------------
 * PLAYER ACTIONS
 *--------------------------------------------------*/

    public void fold(IPlayerActions player) {
        write("%s: folds", player.getPlayerName());
        foldStreet.put(player, currentStreet);
    }

    public void check(IPlayerActions player) {
        write("%s: checks", player.getPlayerName());
    }

    public void call(IPlayerActions player) {
        write("%s: calls %d",
                player.getPlayerName(),
                player.getMoneyOnBet());
    }

    public void raise(IPlayerActions player) {
        write("%s: raises to %d",
                player.getPlayerName(),
                player.getMoneyOnBet());
    }

    public void allIn(IPlayerActions player) {
        write("%s: is all-in %d",
                player.getPlayerName(),
                player.getMoneyOnBet());
    }
        
    public void winner(IPlayerActions player, int prize) {
      winnerPrize.put(player, prize);
    }
    /*--------------------------------------------------
     * EXPERIMENT DATA
     *--------------------------------------------------*/

    public void experimentData() {

        // TODO
    }

    /*--------------------------------------------------
     * WRITE
     *--------------------------------------------------*/

    private void write(String text) {
        log.info(text);
    }

    private void write(String format, Object... args) {
        log.info(String.format(format, args));
    }

    private void blankLine() {
        log.info("");
    }

  
    /*--------------------------------------------------
    * BOARD STREETS
    *--------------------------------------------------*/

    public void flop(Card[] tableCards) {

          currentStreet = Street.FLOP;
        blankLine();

        write("*** FLOP *** %s %s %s",
                tableCards[0].toLetterString(),
                tableCards[1].toLetterString(),
                tableCards[2].toLetterString());
    }

    public void turn(Card[] tableCards) {

        currentStreet = Street.TURN;
        blankLine();


        write("*** TURN *** %s %s %s  %s",
                tableCards[0].toLetterString(),
                tableCards[1].toLetterString(),
                tableCards[2].toLetterString(),
                tableCards[3].toLetterString());
    }

    public void river(Card[] tableCards) {

        currentStreet = Street.RIVER;
        blankLine();

        write("*** RIVER *** %s %s %s %s  %s",
                tableCards[0].toLetterString(),
                tableCards[1].toLetterString(),
                tableCards[2].toLetterString(),
                tableCards[3].toLetterString(),
                tableCards[4].toLetterString());
    }

    public void showdown() {

        blankLine();

        write("*** SHOW DOWN ***");
    }

    public void summary(Card[] tableCards) {

        blankLine();
        write("*** SUMMARY ***");
        blankLine();

        write("Total pot: %d", playerList.getTotalPot());
        blankLine();

       write("Board: [%s]", boardToString(tableCards));

        blankLine();

        for (Player player : players) {

            if (player.isEliminated())
             continue;

            StringBuilder line = new StringBuilder();

            line.append(String.format(
                "Seat %d: %s (%s)",
                playerSeats.get(player),
                player.getPlayerName(),
                mapRole(player.getRole())
            ));

            if (player.isFolded()) {
                line.append(String.format(" - Fold (%s)", foldStreet.get(player)));
            }
           else if (winnerPrize.containsKey(player)) {
                line.append(String.format(" - Winner (+%d)", winnerPrize.get(player)));
            }
            else {
                line.append(" - Lost at Showdown");
            }

            write(line.toString());
        }

        blankLine();
    }

    protected String mapRole(PlayerRole role) {
        return switch (role) {
            case DEALER -> "BTN";
            case SMALL_BLIND -> "SB";
            case BIG_BLIND -> "BB";
            case UNDER_THE_GUN -> "UTG";
            case UNDER_THE_GUN_1 -> "UTG+1";
            case UNDER_THE_GUN_2 -> "UTG+2";
            case LOJACK -> "LJ";
            case HIJACK -> "HJ";
            case CUT_OFF -> "CO";
            default -> "UNKNOWN";
        };
    }

    private String boardToString(Card[] board) {

        StringBuilder sb = new StringBuilder();

        for (Card card : board) {

            if (card == null)
                break;

            if (!sb.isEmpty())
                sb.append(' ');

            sb.append(card.toLetterString());
        }

        return sb.toString();
    }

    
}