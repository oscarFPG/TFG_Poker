package com.ucm.common;


public class GameType {
    
    private GameType(){}

    // Server port
    public static final int PORT = 5005;

    // Types
    public static final byte DATA_TYPE_PETITION = 1;   // Flag to send a pregame petition
    public static final byte DATA_TYPE_NAME = 2;   // Flag to send a name as a string

    // Clients and server codes during pregame
    public static final int CREATE_PETITION = 10;       // Client requests creating a game
    public static final int CREATE_PETITION_ERROR_ALREADY_A_GAME = 11;   // Server error response to CREATE_PETITION

    public static final int JOIN_PETITION = 20;         // Client requests joining a game
    public static final int JOIN_PETITION_ERROR = 21;   // Server error response to JOIN_PETITION

    public static final int ADD_BOT_PETITION = 30;      // Match administrator wants to add a bot to the game
    public static final int ADD_BOT_PETITION_ERROR = 31;
    public static final int BOT_TYPE_LLM = 32;

    public static final int HOST_START_GAME_PETITION = 40;   // Client requests starting the game
    public static final int HOST_START_GAME_PETITION_ERROR_NOT_ENOUGH_PLAYERS = 41;   // Server error response to HOST_START_GAME_PETITION
    public static final int HOST_START_GAME_PETITION_ERROR_NOT_HOST = 42;   // Server error response to HOST_START_GAME_PETITION

    // Player roles
    public static final int PLAYER_ROLE_NO_ROLE = 50;
    public static final int PLAYER_ROLE_DEALER = 51;
    public static final int PLAYER_ROLE_SMALL_BLIND = 52;
    public static final int PLAYER_ROLE_BIG_BLIND = 53;
    public static final int PLAYER_ROLE_UNDER_THE_GUN = 54;
    public static final int PLAYER_ROLE_MIDDLE_POSITION = 55;
    public static final int PLAYER_ROLE_CUT_OFF = 56;

    // Card suits
    public static final int HEARTS = 100;
    public static final int DIAMONDS = 101;
    public static final int CLUBS = 102;
    public static final int SPADES = 103;

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

    // Round, hand and game status
    public static final int GAME_STARTS = 300;
    public static final int ROUND_STARTS = 301;
    public static final int HAND_STARTS = 302;
    public static final int GAME_KEEPS = 303;
    public static final int GAME_ENDS = 304;
    public static final int ROUND_ENDS = 305;
    public static final int HAND_ENDS = 306;
    public static final int HAND_ENDS_BY_FOLD = 310;
    public static final int PLAYER_WINS_HAND = 320;
    public static final int PLAYER_LOSES_HAND = 321;
    public static final int PLAYER_WINS_GAME = 330;
    public static final int PLAYER_LOSES_GAME = 331;

    // Player actions
    public static final int FOLD_ACTION = 400;
    public static final int CHECK_ACTION = 401;
    public static final int ALL_IN_ACTION = 402;
    public static final int CALL_ACTION = 403;
    public static final int RAISE_ACTION = 404;

}