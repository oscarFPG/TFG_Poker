package com.ucm.common;

/**
 * Represents the different streets (betting rounds) of a Texas Hold'em poker
 * hand.
 * <p>
 * Each street is associated with a network code used for communication between
 * the client and server. The enum also provides a utility method to determine
 * the next street in the normal progression of a hand.
 * </p>
 */
public enum PokerStreet {
    
    /**
     * Preflop round, before any community cards are dealt. Players receive their hole cards.
     */
    PREFLOP {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_PREFLOP;
        }
    },
    /**
     * Flop round, after the first three community cards are dealt. Players can use these cards along with their hole cards to make the best hand.
     */
    FLOP {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_FLOP;
        }
    },
    /**
     * Turn round, after the fourth community card is dealt. Players can use this card along with their hole cards and the flop to make the best hand.
     */
    TURN {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_TURN;
        }
    },
    /**
     * River round, after the fifth and final community card is dealt. Players can use this card along with their hole cards, the flop, and the turn to make the best hand.
     */
    RIVER {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_RIVER;
        }
    },
    /**
     * Showdown round, after all betting rounds are complete. Players reveal their hole cards and the best hand wins the pot.
     */
    SHOWDOWN {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_SHOWDOWN;
        }
    };
    /**
     * Returns the network code associated with this poker street.
     *
     * @return the network code used to identify the street in the
     *         communication protocol
     */
    public abstract int getNetworkCode();
    /**
     * Returns the next street in the normal progression of a poker hand.
     * <p>
     * The progression is:
     * </p>
     * <pre>
     * PREFLOP → FLOP → TURN → RIVER → SHOWDOWN → PREFLOP
     * </pre>
     *
     * @param current the current poker street
     * @return the next poker street in the sequence
     */
    public static PokerStreet nextRound(final PokerStreet current) {

        switch (current) {
        case PREFLOP:
            return FLOP;

        case FLOP:
            return TURN;

        case TURN:
            return RIVER;

        case RIVER:
            return SHOWDOWN;

        case SHOWDOWN:
            return PREFLOP;
    
        default:
            return current;
        }
    }

}