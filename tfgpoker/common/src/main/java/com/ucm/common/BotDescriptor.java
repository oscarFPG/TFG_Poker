package com.ucm.common;

public record BotDescriptor (int botId, String botName, String shortDescription, String fullDescription, String iconPath, boolean allowStyles) {}
