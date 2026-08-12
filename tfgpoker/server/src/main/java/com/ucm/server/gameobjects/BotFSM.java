package com.ucm.server.gameobjects;

/**
 * Parent class for all finite state machine (FSM) based bots in the poker game. 
 * This class extends the {@link Bot} class and provides a framework for implementing bots that operate based on a finite state machine model.
 */
public abstract class BotFSM extends Bot {

    /**
     * Constructs a new BotFSM instance with the specified bot identifier.
     * @param botID the unique identifier for the bot
     */
    public BotFSM(int botID) {
        super(botID);
    }

    /**
     * Returns the type of player, which is "BOT_FSM" for all finite state machine based bot instances.
     * @return "BOT_FSM"
     */
    @Override
    public String getPlayerType() {
        return "BOT_FSM";
    }

}