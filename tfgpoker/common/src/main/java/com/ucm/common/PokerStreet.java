package com.ucm.common;

public enum PokerStreet {
    
    PREFLOP {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_PREFLOP;
        }
    },

    FLOP {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_FLOP;
        }
    },

    TURN {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_TURN;
        }
    },

    RIVER {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_RIVER;
        }
    },

    SHOWDOWN {
        @Override
        public int getNetworkCode() {
            return GameType.GAME_ROUND_SHOWDOWN;
        }
    };

    public abstract int getNetworkCode();

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
    
        default:    // Not happening
            return current;
        }
    }

}