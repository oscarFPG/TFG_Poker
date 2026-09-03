package com.ucm.common;

/**
 * Immutable data structure that represents a bot participating in a match.
 * <p>
 * A {@code BotStruct} contains the bot identifier, the match it belongs to,
 * its display name, and the playing style used by the bot.
 * </p>
 * <p>
 * This record also provides factory methods for creating bots with either the
 * default style or a specific style.
 * </p>
 *
 * @param botId unique identifier of the bot
 * @param matchId identifier of the match in which the bot participates
 * @param botName display name of the bot
 * @param style playing style assigned to the bot
 */
public record BotStruct(int botId, int matchId, String botName, BotStyle style) {

    /**
     * Creates a bot using the default playing style.
     *
     * @param botId unique identifier of the bot
     * @param matchId identifier of the match in which the bot participates
     * @param botName display name of the bot
     * @return a new {@code BotStruct} configured with {@link BotStyle#DEFAULT}
     */
    public static BotStruct createSimpleBot(int botId, int matchId, String botName) {
        return new BotStruct(botId, matchId, botName, BotStyle.DEFAULT);
    }
    /**
     * Creates a bot with the specified playing style.
     *
     * @param botId unique identifier of the bot
     * @param matchId identifier of the match in which the bot participates
     * @param botName display name of the bot
     * @param style playing style assigned to the bot
     * @return a new {@code BotStruct} with the provided configuration
     */
    public static BotStruct createStyledBot(int botId, int matchId, String botName, BotStyle style) {
        return new BotStruct(botId, matchId, botName, style);
    }
}