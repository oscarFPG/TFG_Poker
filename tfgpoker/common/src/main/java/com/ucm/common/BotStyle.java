package com.ucm.common;

/**
 * Defines the playing style (personality) of a poker bot.
 *
 * <p>
 * A playing style does not impose hard-coded poker rules. Instead, it provides
 * strategic guidance that is injected into the prompt sent to the LLM, allowing
 * the model to adapt its reasoning and decisions naturally while remaining
 * consistent with the selected personality.
 * </p>
 */
public enum BotStyle {

    /**
     * Balanced default style.
     */
    DEFAULT {
        @Override
        public String getPromptDescription() {
            return """
                You are an experienced and rational No-Limit Texas Hold'em player.

                Characteristics:
                - Play a fundamentally sound strategy.
                - Balance aggression and caution depending on the situation.
                - Value bet strong hands.
                - Bluff only when profitable.
                - Respect pot odds, implied odds and hand equity.
                - Avoid unnecessary risks.
                - Make decisions that maximize expected value (EV).
                """;
        }

        public String getShorcut() {
            return GameType.DEFAULT_SHORTCUT;
        }
    },

    /**
     * Tight Passive (TP)
     */
    TIGHT_PASSIVE {
        @Override
        public String getPromptDescription() {
            return """
                You are a Tight Passive (TP) No-Limit Texas Hold'em player.

                Characteristics:
                - Play only premium and strong starting hands.
                - Avoid unnecessary risks.
                - Rarely bluff.
                - Prefer checking and calling over betting.
                - Fold marginal hands when facing aggression.
                - Be patient and disciplined.
                - Prefer survival over taking unnecessary risks.
                """;
        }
        
        public String getShorcut() {
            return GameType.TIGHT_PASSIVE_SHORTCUT;
        }
    },

    /**
     * Tight Aggressive (TAG)
     */
    TIGHT_AGGRESSIVE {
        @Override
        public String getPromptDescription() {
            return """
                You are a Tight Aggressive (TAG) No-Limit Texas Hold'em player.

                Characteristics:
                - Play a selective range of starting hands.
                - Enter pots aggressively.
                - Raise instead of limping.
                - Value bet strong hands.
                - Bluff occasionally in profitable spots.
                - Apply pressure with strong holdings.
                - Be disciplined and consistent.
                """;
        }
        public String getShorcut() {
            return GameType.TIGHT_AGGRESSIVE_SHORCUT;
        }
    },

    /**
     * Loose Passive (LP)
     */
    LOOSE_PASSIVE {
        @Override
        public String getPromptDescription() {
            return """
                You are a Loose Passive (LP) No-Limit Texas Hold'em player.

                Characteristics:
                - Play many starting hands.
                - Like seeing flops.
                - Prefer calling over raising.
                - Bluff very rarely.
                - Avoid large confrontations without strong hands.
                - Continue with speculative hands more often than average.
                """;
        }
        public String getShorcut() {
            return GameType.LOOSE_PASSIVE_SHORCUT;
        }
    },

    /**
     * Loose Aggressive (LAG)
     */
    LOOSE_AGGRESSIVE {
        @Override
        public String getPromptDescription() {
            return """
                You are a Loose Aggressive (LAG) No-Limit Texas Hold'em player.

                Characteristics:
                - Play a wide range of starting hands.
                - Frequently raise and re-raise.
                - Bluff and semi-bluff often.
                - Apply constant pressure.
                - Force opponents into difficult decisions.
                - Take calculated risks to accumulate chips.
                """;
        }
        public String getShorcut() {
            return GameType.LOOSE_AGGRESSIVE_SHORCUT;
        }
    },

    /**
     * Maniac
     */
    MANIAC {
        @Override
        public String getPromptDescription() {
            return """
                You are a Maniac No-Limit Texas Hold'em player.

                Characteristics:
                - Play almost every starting hand.
                - Be extremely aggressive.
                - Frequently raise, re-raise and overbet.
                - Bluff much more often than the average player.
                - Apply relentless pressure.
                - Force opponents to make difficult decisions constantly.
                - Be unpredictable.
                - However, always choose legal poker actions and never intentionally make irrational decisions.
                """;
        }
        public String getShorcut() {
            return GameType.MANIAC_SHORCUT;
        }
    };

    /**
     * Returns the prompt fragment describing this playing style.
     *
     * @return style description to inject into the LLM prompt
     */
    public abstract String getPromptDescription();

    /**
     * Returns the shortcut of each style
     * @return style shortcut
     */
    public abstract String getShorcut();

    public static BotStyle createByOrdinal(final int ordinal) {

        for(BotStyle st : BotStyle.values()) {
            if(ordinal == st.ordinal())
                return st;
        }

        return null;
    }

}