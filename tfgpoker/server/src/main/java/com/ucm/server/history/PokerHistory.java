package com.ucm.server.history;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * Writes PokerStars-style hand histories.
 */
public final class PokerHistory {

    private static final Logger history = LogManager.getLogger("PokerHistory");

    private PokerHistory() {
    }

    /*--------------------------------------------------
     * MATCH
     *--------------------------------------------------*/

    public static void startMatch(int matchId) {

        ThreadContext.put("match", String.format("%04d", matchId));
    }

    public static void endMatch() {

        ThreadContext.clearAll();
    }

    /*--------------------------------------------------
     * HAND
     *--------------------------------------------------*/

    public static void startHand(int handId) {

        ThreadContext.put("hand", String.format("%04d", handId));
    }

    public static void endHand() {

        ThreadContext.remove("hand");
    }

    /*--------------------------------------------------
     * WRITE
     *--------------------------------------------------*/

    public static void write(String text) {
        history.info(text);
    }

    public static void write(String format, Object... args) {
        history.info(format, args);
    }

    public static void blankLine() {
        history.info("");
    }
}