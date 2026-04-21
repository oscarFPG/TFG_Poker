package com.ucm.common;


public class GameType {
    
    private GameType(){}

    /* ---------------- SERVER PROPERTIES ----------------  */
    public static final int PORT = 5005;
    public static final int MAX_PLAYERS = 9;

    /* ---------------- BOOLEAN TYPES ----------------*/
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    /* ---------------- PREGAME CODES ----------------  */
    public static final int PETITION_PLAYER_NAME = 1;
    public static final int PETITION_CREATE_GAME = 2;
    public static final int PETITION_JOIN_GAME = 3;
    public static final int PETITION_ADD_BOTS = 4;
    public static final int PETITION_NOT_ADD_BOTS = 5;

    public static final int EVENT_TYPE = 100;
    public static final int EVENT_PLAYER_JOINED = 101;
    public static final int EVENT_GAME_STARTS = 102;

    public static final int CONFIRMATION_TYPE = 200;
    public static final int CONFIRMATION_NAME_VALID = 201;
    public static final int CONFIRMATION_WAITING_GAME = 202;
    public static final int CONFIRMATION_HOST_PLAYER = 203;
    public static final int CONFIRMATION_NO_HOST_PLAYER = 204;
    public static final int CONFIRMATION_GAME_STARTS = 205;
    public static final int CONFIRMATION_PLAYER_STARTS = 206;

    public static final int ERROR_TYPE = 500;
    public static final int ERROR_NAME_TOO_SHORT = 501;     // Name error codes
    public static final int ERROR_NAME_TOO_LONG = 502;
    public static final int ERROR_GAME_NOT_CREATED = 503;
    public static final int ERROR_GAME_NOT_JOINED = 504;
    public static final int ERROR_GAME_CANNOT_START = 505;
    public static final int ERROR_GAME_CANCELS = 506;
     public static final int ERROR_NAME_ALREADY_USED = 507;

    /* ---------------- GAME CODES ----------------  */
    // Player roles
    public static final int PLAYER_ROLE_UNDER_THE_GUN = 80;
    public static final int PLAYER_ROLE_UNDER_THE_GUN_1 = 81;
    public static final int PLAYER_ROLE_UNDER_THE_GUN_2 = 82;
    public static final int PLAYER_ROLE_LOJACK = 83;
    public static final int PLAYER_ROLE_HIJACK = 84;
    public static final int PLAYER_ROLE_CUT_OFF = 85;
    public static final int PLAYER_ROLE_DEALER = 86;
    public static final int PLAYER_ROLE_SMALL_BLIND = 87;
    public static final int PLAYER_ROLE_BIG_BLIND = 88;

    // Card suits
    public static final int HEARTS = 90;
    public static final int DIAMONDS = 91;
    public static final int CLUBS = 92;
    public static final int SPADES = 93;

    // Card numbers
    public static final int NUMBER_ACE = 110;
    public static final int NUMBER_TWO = 111;
    public static final int NUMBER_THREE = 112;
    public static final int NUMBER_FOUR = 113;
    public static final int NUMBER_FIVE = 114;
    public static final int NUMBER_SIX = 115;
    public static final int NUMBER_SEVEN = 116;
    public static final int NUMBER_EIGHT = 117;
    public static final int NUMBER_NINE = 118;
    public static final int NUMBER_TEN = 119;
    public static final int NUMBER_J = 120;
    public static final int NUMBER_Q = 121;
    public static final int NUMBER_K = 122;

    // Turns
    public static final int TURN_PLAY = 200;
    public static final int TURN_FORCED_SB = 201;
    public static final int TURN_FORCED_BB = 202;
    public static final int TURN_WAIT = 203;
    public static final int TURN_FOLD = 204;
    public static final int TURN_OTHER_PLAYER = 205;
    public static final int TURN_BEFORE_PLAY = 206;

    // Round, hand and game status
    public static final int GAME_STARTS = 300;
    public static final int GAME_KEEPS = 303;
    public static final int GAME_ENDS = 304;
    public static final int ROUND_ENDS = 305;
    public static final int HAND_ENDS_BY_FOLD = 310;
    public static final int MY_PLAYER_STATUS = 311;
    public static final int OTHER_PLAYER_STATUS = 312;
    public static final int PLAYER_STATUS_END = 313;
    public static final int NEW_ROUND = 314;
    public static final int PLAYER_WINS_GAME = 330;
    public static final int PLAYER_LOSES_GAME = 331;
    public static final int TOTAL_POT = 332;

    // Player actions
    public static final String FOLD_ACTION_FULL = "fold";
    public static final String FOLD_ACTION_SHORTCUT = "f";
    public static final String CHECK_ACTION_FULL = "check";
    public static final String CHECK_ACTION_SHORTCUT = "k";
    public static final String ALL_IN_ACTION_FULL = "all-in";
    public static final String ALL_IN_ACTION_SHORTCUT = "a";
    public static final String CALL_ACTION_FULL = "call";
    public static final String CALL_ACTION_SHORTCUT = "c";
    public static final String RAISE_ACTION_FULL = "raise"; // This requires an amount parameter: e.g: "raise 100"
    public static final String RAISE_ACTION_SHORTCUT = "r"; // This requires an amount parameter: e.g: "raise 100"

    // Bots identifier
    public static final int BOT_GEMINI = 10000;
    public static final int BOT_LLAMA = 10001;

    //Equity identifier
    public static final int EQUITY_UPDATE = 40000;

    //Turn timer left identifier
    public static final int TURN_TIMER_UPDATE = 40100;
}