package com.ucm.server.gameobjects;

/**
 * Enumeration that represents different playing styles for a poker bot.
 * 
 * <p>
 * Each style defines a specific strategy profile, including:
 * </p>
 * <ul>
 * <li>A textual description of the behavior</li>
 * <li>A raise sizing strategy based on the pot</li>
 * <li>A folding threshold based on hand equity</li>
 * </ul>
 * 
 * <p>
 * These styles are used to simulate different types of opponents,
 * ranging from conservative players to highly aggressive ones.
 * </p>
 */
public enum BotStyle {

    /**
     * Tight playing style.
     * 
     * <p>
     * Focuses on strong hands and avoids marginal situations.
     * Typically folds often and plays only premium holdings.
     * </p>
     */
    TIGHT {
        @Override
        public String getDescription() {
            return "Plays very selectively, focusing only on strong hands and avoiding marginal situations.";
        }

        @Override
        public double getRaiseSizing(double pot) {
            return pot * 0.5;
        }

        @Override
        public boolean shouldFoldLowEquity(double equity) {
            return equity < 0.15;
        }
    },

    /**
     * Passive playing style.
     * 
     * <p>
     * Prefers calling and checking rather than raising.
     * Only becomes aggressive with very strong hands.
     * </p>
     */
    PASSIVE {
        @Override
        public String getDescription() {
            return "Prefers calling and checking, avoiding aggressive plays unless very strong.";
        }

        @Override
        public double getRaiseSizing(double pot) {
            return pot * 0.4;
        }

        @Override
        public boolean shouldFoldLowEquity(double equity) {
            return equity < 0.10;
        }
    },

    /**
     * Aggressive playing style.
     * 
     * <p>
     * Frequently applies pressure by raising with a wide range of hands.
     * Aims to force opponents into difficult decisions.
     * </p>
     */
    AGGRESSIVE {
        @Override
        public String getDescription() {
            return "Applies pressure frequently, raising with a wide range of hands.";
        }

        @Override
        public double getRaiseSizing(double pot) {
            return pot * 0.75;
        }

        @Override
        public boolean shouldFoldLowEquity(double equity) {
            return equity < 0.05;
        }
    },

    /**
     * Maniac playing style.
     * 
     * <p>
     * Extremely aggressive strategy that frequently bluffs and overbets.
     * Rarely folds, regardless of hand strength.
     * </p>
     */
    MANIAC {
        @Override
        public String getDescription() {
            return "Plays extremely aggressively, often bluffing and applying maximum pressure.";
        }

        @Override
        public double getRaiseSizing(double pot) {
            return pot * 1.2;
        }

        @Override
        public boolean shouldFoldLowEquity(double equity) {
            return false; 
        }
    };

    /**
     * Returns a textual description of the playing style.
     * 
     * @return {@link String} describing the strategy
     */
    public abstract String getDescription();

    /**
     * Computes the raise sizing based on the current pot.
     * 
     * @param pot current pot size
     * @return amount to raise
     */
    public abstract double getRaiseSizing(double pot);

    /**
     * Determines whether the bot should fold based on its equity.
     * 
     * @param equity probability of winning the hand
     * @return true if the bot should fold, false otherwise
     */
    public abstract boolean shouldFoldLowEquity(double equity);

    /**
     * Adjusts a raise size to the player's stack.
     * This prevents betting more money than available.
     * 
     * @param size  proposed bet size
     * @param stack player's remaining stack
     * @return adjusted bet size (capped by stack)
     */
    public double adjustSizingToStack(double size, double stack) {
        return Math.min(size, stack);
    }
}