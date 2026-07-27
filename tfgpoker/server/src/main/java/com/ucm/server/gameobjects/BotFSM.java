package com.ucm.server.gameobjects;

import com.ucm.common.GameType;

public abstract class BotFSM extends Bot {

    public BotFSM(int botID) {
        super(botID);
    }

    public final String nextState_FOLD(){ return GameType.FOLD_ACTION_FULL; }
    public final String nextState_CALL(){ return GameType.CALL_ACTION_FULL; }
    public final String nextState_CHECK(){ return GameType.CHECK_ACTION_FULL; }
    public final String nextState_RAISE(final int amount){ return GameType.RAISE_ACTION_FULL + String.valueOf(amount); }
    public final String nextState_ALL_IN(){ return GameType.ALL_IN_ACTION_FULL; }

    @Override
    public String getPlayerType() {
        return "BOT_FSM";
    }

}