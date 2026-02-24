package com.ucm.control;

import com.ucm.GameType;
import com.ucm.gameobjects.PlayerRole;

/**
 * Translates all in-game types(Classes, Records, etc...) in communication types(Any communication-oriented type defined in GameTypes.h)
 */
public class GameAdapter {
    
    private GameAdapter(){}


    public static PlayerRole translatePlayerRole(int rolCode){
        switch (rolCode) {
        case GameType.PLAYER_ROLE_NO_ROLE:
            return PlayerRole.NO_ROLE;
        case GameType.PLAYER_ROLE_DEALER:
            return PlayerRole.DEALER;
        case GameType.PLAYER_ROLE_SMALL_BLIND:
            return PlayerRole.SMALL_BLIND;
        case GameType.PLAYER_ROLE_BIG_BLIND:
            return PlayerRole.BIG_BLIND;
        case GameType.PLAYER_ROLE_UNDER_THE_GUN:
            return PlayerRole.UNDER_THE_GUN;
        case GameType.PLAYER_ROLE_MIDDLE_POSITION:
            return PlayerRole.MIDDLE_POSITION;
        case GameType.PLAYER_ROLE_CUT_OFF:
            return PlayerRole.CUT_OFF;
        default:
            return null;
        }
    }

    public static int playerRoleToCode(PlayerRole r){
        switch (r) {
        case PlayerRole.NO_ROLE:
            return GameType.PLAYER_ROLE_NO_ROLE;
        case PlayerRole.DEALER:
            return GameType.PLAYER_ROLE_DEALER;
        case PlayerRole.SMALL_BLIND:
            return GameType.PLAYER_ROLE_SMALL_BLIND;
        case PlayerRole.BIG_BLIND:
            return GameType.PLAYER_ROLE_BIG_BLIND;
        case PlayerRole.UNDER_THE_GUN:
            return GameType.PLAYER_ROLE_UNDER_THE_GUN;
        case PlayerRole.MIDDLE_POSITION:
            return GameType.PLAYER_ROLE_MIDDLE_POSITION;
        case PlayerRole.CUT_OFF:
            return GameType.PLAYER_ROLE_CUT_OFF;
        default:
            return -1;
        }
    }

}