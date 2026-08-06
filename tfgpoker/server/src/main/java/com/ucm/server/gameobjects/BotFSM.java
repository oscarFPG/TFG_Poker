package com.ucm.server.gameobjects;

public abstract class BotFSM extends Bot {

    
    public BotFSM(int botID) {
        super(botID);
    }

    @Override
    public String getPlayerType() {
        return "BOT_FSM";
    }

}