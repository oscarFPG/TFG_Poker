package com.ucm.server.control;

import com.ucm.common.GameType;



public class GameAdapter {
    
    private GameAdapter() {}

    // Player turns and game states
    public static int playerTurnPlayToCode() {
        return GameType.TURN_PLAY;
    }
    public static int playerTurnWaitToCode() {
        return GameType.TURN_WAIT;
    }
    public static int playerTurnForcedSBToCode() {
        return GameType.TURN_FORCED_SB;
    }
    public static int playerTurnForcedBBToCode() {
        return GameType.TURN_FORCED_BB;
    }
    public static int gameRoundEnded() {
        return GameType.ROUND_ENDS;
    }
    public static int gameHandEnded() {
        return GameType.HAND_ENDS;
    }
    public static int gameEnded() {
        return GameType.GAME_ENDS;
    }
    public static int gameKeeps() {
        return GameType.GAME_KEEPS;
    }
    public static int playerWinsHand() {
        return GameType.PLAYER_WINS_HAND;
    }
    public static int playerLosesHand() {
        return GameType.PLAYER_LOSES_HAND;
    }
    public static int playerWinsGame() {
        return GameType.PLAYER_WINS_GAME;
    }
    public static int playerLosesGame() {
        return GameType.PLAYER_LOSES_GAME;
    }
    public static int handEndsByFolds() {
        return GameType.HAND_ENDS_BY_FOLD;
    }


}