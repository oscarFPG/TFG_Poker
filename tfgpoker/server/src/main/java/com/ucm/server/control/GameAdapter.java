package com.ucm.server.control;

import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;


public class GameAdapter {
    
    private GameAdapter() {}


    // Player roles
    public static PlayerRole translatePlayerRole(int rolCode) {
        switch (rolCode) {
        case GameType.PLAYER_ROLE_DEALER:
            return PlayerRole.DEALER;
        case GameType.PLAYER_ROLE_SMALL_BLIND:
            return PlayerRole.SMALL_BLIND;
        case GameType.PLAYER_ROLE_BIG_BLIND:
            return PlayerRole.BIG_BLIND;
        case GameType.PLAYER_ROLE_UNDER_THE_GUN:
            return PlayerRole.UNDER_THE_GUN;
        case GameType.PLAYER_ROLE_UNDER_THE_GUN_1:
            return PlayerRole.UNDER_THE_GUN_1;
        case GameType.PLAYER_ROLE_UNDER_THE_GUN_2:
            return PlayerRole.UNDER_THE_GUN_2;
        case GameType.PLAYER_ROLE_LOJACK:
            return PlayerRole.LOJACK;
        case GameType.PLAYER_ROLE_HIJACK:
            return PlayerRole.HIJACK;
        case GameType.PLAYER_ROLE_CUT_OFF:
            return PlayerRole.CUT_OFF;
        default:
            return null;
        }
    }
    public static int playerRoleToCode(PlayerRole r) {
        switch (r) {
        case PlayerRole.DEALER:
            return GameType.PLAYER_ROLE_DEALER;
        case PlayerRole.SMALL_BLIND:
            return GameType.PLAYER_ROLE_SMALL_BLIND;
        case PlayerRole.BIG_BLIND:
            return GameType.PLAYER_ROLE_BIG_BLIND;
        case PlayerRole.UNDER_THE_GUN:
            return GameType.PLAYER_ROLE_UNDER_THE_GUN;
        case PlayerRole.UNDER_THE_GUN_1:
            return GameType.PLAYER_ROLE_UNDER_THE_GUN_1;
        case PlayerRole.UNDER_THE_GUN_2:
            return GameType.PLAYER_ROLE_UNDER_THE_GUN_2;
        case PlayerRole.LOJACK:
            return GameType.PLAYER_ROLE_LOJACK;
        case PlayerRole.HIJACK:
            return GameType.PLAYER_ROLE_HIJACK;
        case PlayerRole.CUT_OFF:
            return GameType.PLAYER_ROLE_CUT_OFF;
        default:
            return -1;
        }
    }

    // Cards
    public static int cardValueToCode(Card c) {
        switch (c.getNumber()) {
        case 1:     // As
            return GameType.NUMBER_ACE;
        case 2:
            return GameType.NUMBER_TWO;
        case 3:
            return GameType.NUMBER_THREE;
        case 4:
            return GameType.NUMBER_FOUR;
        case 5:
            return GameType.NUMBER_FIVE;
        case 6:
            return GameType.NUMBER_SIX;
        case 7:
            return GameType.NUMBER_SEVEN;
        case 8:
            return GameType.NUMBER_EIGHT;
        case 9:
            return GameType.NUMBER_NINE;
        case 10:
            return GameType.NUMBER_TEN;
        case 11:    // J
            return GameType.NUMBER_J;
        case 12:    // Q
            return GameType.NUMBER_Q;
        case 13:    // K
            return GameType.NUMBER_K;
        default:
            return -1;
        }
    }
    public static int cardSuitToCode(Card c) {
        switch (c.getSuit()) {
        case Suit.SPADES:
            return GameType.SPADES;
        case Suit.HEARTS:
            return GameType.HEARTS;
        case Suit.DIAMONDS:
            return GameType.DIAMONDS;
        case Suit.CLUBS:
            return GameType.CLUBS;
        default:
            return -1;
        }
    }

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

    // Commands
    public static int commandFoldToCode() {
        return GameType.FOLD_ACTION;
    }
    public static int commandCheckToCode() {
        return GameType.CHECK_ACTION;
    }
    public static int commandAllInToCode() {
        return GameType.ALL_IN_ACTION;
    }
    public static int commandCallToCode() {
        return GameType.CALL_ACTION;
    }
    public static int commandRaiseToCode() {
        return GameType.RAISE_ACTION;
    }

}