package com.ucm.common;


public class GameType {
    
    private GameType(){}

    // Server port
    public static final int PORT = 5005;

    // Types
    public static final byte DATA_TYPE_PETITION = 1;   // Flag to send a pregame petition
    public static final byte DATA_TYPE_NAME = 2;   // Flag to send a name as a string

    // Clients and server codes during pregame
    public static final int CREATE_PETITION = 10;   // Client requests creating a game
    public static final int CREATE_PETITION_ERROR_ALREADY_A_GAME = 11;   // Server error response to CREATE_PETITION

    public static final int JOIN_PETITION = 20;   // Client requests joining a game
    public static final int JOIN_PETITION_ERROR = 21;   // Server error response to JOIN_PETITION

    public static final int HOST_START_GAME_PETITION = 30;   // Client requests starting the game
    public static final int HOST_START_GAME_PETITION_ERROR_NOT_ENOUGH_PLAYERS = 31;   // Server error response to HOST_START_GAME_PETITION
    public static final int HOST_START_GAME_PETITION_ERROR_NOT_HOST = 32;   // Server error response to HOST_START_GAME_PETITION

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
    public static final int GAME_ENDS = 303;
    public static final int ROUND_ENDS = 304;
    public static final int HAND_ENDS = 305;
    
    // Player actions
    public static final int FOLD_ACTION = 300;
    public static final int CHECK_ACTION = 301;
    public static final int ALL_IN_ACTION = 302;
    public static final int CALL_ACTION = 303;
    public static final int RAISE_ACTION = 304;

    /*
    // ES PREFLOP
    public static final int PRE_FLOP = 6;
    // NO ES PREFLOP
    public static final int NO_PRE_FLOP = 6;

    // TURN_OTHER_PLAYER
    public static final int TURN_OTHER_PLAYER = 22;
    // END_OF_ROUND (PRE-FLOP, FLOP, RIVER, ETC)
    public static final int END_OF_ROUND = 23;
    // END_OF_HAND (SHOW_DOWN)
    public static final int END_OF_HAND = 24;

    // END_OF_GAME (SE ACABA TODO EL JUEGO)
    public static final int END_OF_GAME = 25;
    

    // MAX_TIME AVISA AL CLIENTE QUE SE HA QUEDADO SIN TIEMPO
    public static final int MAX_TIME = 40;
    
    // HAND OVER WIN
    public static final int HAND_OVER_WIN = 60;
    // HAND OVER LOSE
    public static final int HAND_OVER_LOSE = 61;
    // GAME OVER WIN
    public static final int GAME_OVER_WIN = 62;
    // GAME OVER LOSE
    public static final int GAME_OVER_LOSE = 63;

    // ADMIN PLAYER
    public static final int PLAYER_IS_ADMIN = 70;
    // NOT ADMIN PLAYER
    public static final int PLAYER_NOT_ADMIN = 76;
    // GAME START ADMINISTRATOR
    public static final int GAME_START_ADMINISTRATOR = 71;
    // FULL TABLE
    public static final int FULL_TABLE = 73;
    // WAIT_GAME
    public static final int WAIT_GAME = 74;

    */
}