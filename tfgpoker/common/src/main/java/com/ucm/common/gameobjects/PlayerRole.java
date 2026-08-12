package com.ucm.common.gameobjects;

import java.util.ArrayList;
import java.util.List;

import com.ucm.common.GameType;


public enum PlayerRole {

    UNDER_THE_GUN(GameType.PLAYER_ROLE_UNDER_THE_GUN),
    UNDER_THE_GUN_1(GameType.PLAYER_ROLE_UNDER_THE_GUN_1),
    UNDER_THE_GUN_2(GameType.PLAYER_ROLE_UNDER_THE_GUN_2),
    LOJACK(GameType.PLAYER_ROLE_LOJACK),
    HIJACK(GameType.PLAYER_ROLE_HIJACK),
    CUT_OFF(GameType.PLAYER_ROLE_CUT_OFF),
    DEALER(GameType.PLAYER_ROLE_DEALER),
    SMALL_BLIND(GameType.PLAYER_ROLE_SMALL_BLIND),
    BIG_BLIND(GameType.PLAYER_ROLE_BIG_BLIND);


    int networkCode;

    PlayerRole(final int code) {
        networkCode = code;
    }


    public static PlayerRole getPlayerRoleFromCode(final int roleCode) {

        switch (roleCode) {
        case GameType.PLAYER_ROLE_UNDER_THE_GUN:
            return UNDER_THE_GUN;

        case GameType.PLAYER_ROLE_UNDER_THE_GUN_1:
            return UNDER_THE_GUN_1;

        case GameType.PLAYER_ROLE_UNDER_THE_GUN_2:
            return UNDER_THE_GUN_2;

        case GameType.PLAYER_ROLE_LOJACK:
            return LOJACK;

        case GameType.PLAYER_ROLE_HIJACK:
            return HIJACK;

        case GameType.PLAYER_ROLE_CUT_OFF:
            return CUT_OFF;

        case GameType.PLAYER_ROLE_DEALER:
            return DEALER;

        case GameType.PLAYER_ROLE_SMALL_BLIND:
            return SMALL_BLIND;

        case GameType.PLAYER_ROLE_BIG_BLIND:
            return BIG_BLIND;

        default:
            return null;
        }
    }

    public static List<PlayerRole> getRolesDistribution(final int numPlayers) {
        switch (numPlayers) {
            case 2:
                return new ArrayList<>(List.of(
                    SMALL_BLIND,
                    BIG_BLIND
                ));

            case 3:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND
                ));

            case 4:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN
                ));

            case 5:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    CUT_OFF
                ));

            case 6:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    HIJACK,
                    CUT_OFF
                ));

            case 7:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    LOJACK,
                    HIJACK,
                    CUT_OFF
                ));

            case 8:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    UNDER_THE_GUN_1,
                    LOJACK,
                    HIJACK,
                    CUT_OFF
                ));

            case 9:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    UNDER_THE_GUN_1,
                    UNDER_THE_GUN_2,
                    LOJACK,
                    HIJACK,
                    CUT_OFF
                ));

            default:
                return null;
        }
    }

    public int getNetworkCode() {
        return networkCode;
    }

}