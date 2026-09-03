package com.ucm.common;

/**
 * Immutable descriptor containing the metadata required to identify and describe a bot.
 * <p>
 * This record stores the bot's identifier, display name, descriptions, icon path, and a flag indicating whether the bot allows styles.
 * <p>
 * 
 * @param botId unique identifier for the bot
 * @param botName display name of the bot
 * @param shortDescription brief description of the bot
 * @param fullDescription detailed description of the bot's behavior and features
 * @param iconPath path to the bot's icon image
 * @param allowStyles flag indicating whether the bot detailed description of the bot's behavior playing style is allowed
 */
public record BotDescriptor (int botId, String botName, String shortDescription, String fullDescription, String iconPath, boolean allowStyles) {}
