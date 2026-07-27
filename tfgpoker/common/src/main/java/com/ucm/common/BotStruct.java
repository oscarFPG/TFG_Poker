package com.ucm.common;

public record BotStruct (int botId, int matchId, String botName, BotStyle style) {

    public static BotStruct createSimpleBot(int botId, int matchId, String botName) {
        return new BotStruct(botId, matchId, botName, BotStyle.DEFAULT);
    }

    public static BotStruct createStyledBot(int botId, int matchId, String botName, BotStyle style) {
        return new BotStruct(botId, matchId, botName, style);
    }
}