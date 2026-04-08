package com.ucm.common.gameobjects;

import com.ucm.common.GameType;

public enum PlayerRole {

    DEALER(),
    SMALL_BLIND(),
    BIG_BLIND(),
    UNDER_THE_GUN(),
    MIDDLE_POSITION(),
    CUT_OFF(),
    NO_ROLE();

    public static PlayerRole getPlayerRoleFromCode(final int roleCode) {

        switch (roleCode) {
        case GameType.PLAYER_ROLE_DEALER: 
            return DEALER;

        case GameType.PLAYER_ROLE_SMALL_BLIND:
            return SMALL_BLIND;

        case GameType.PLAYER_ROLE_BIG_BLIND:
            return BIG_BLIND;

        case GameType.PLAYER_ROLE_UNDER_THE_GUN:
            return UNDER_THE_GUN;

        case GameType.PLAYER_ROLE_MIDDLE_POSITION:
            return MIDDLE_POSITION;

        case GameType.PLAYER_ROLE_CUT_OFF:
            return CUT_OFF;

        case GameType.PLAYER_ROLE_NO_ROLE:
            return NO_ROLE;
    
        default:
            return null;
        }
    }

}