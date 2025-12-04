package com.ucm;

public class GameType {
    
    private GameType(){}

    // PETITIONS
    public static final int PETITION_CREATE_MATCH = 1001;
    public static final int PETITION_JOIN_MATCH = 1002;
    public static final int PETITION_RECONNECT_MATCH = 1003;
    public static final int PETITION_UNKNOWN = 1004;

    // GENERAL SERVER PORT
    public static final int PORT = 5005;

    // HAND TYPE
    public static final int FOLD = 1;
    public static final int CHECK = 2;
    public static final int ALL_IN = 3;
    public static final int CALL = 4;
    public static final int RAISE = 5;
    public static final int PRE_FLOP = 6;
    public static final int NO_PRE_FLOP = 7;
   
    // CONNECTION STATUS
    public static final int ESTABLISH_CONECTION = 10;
    public static final int CONECTION_ACEPTED = 11;
    public static final int CONECTION_DECLINE = 12;

    // TURN_MOVE
    public static final int TURN_PLAY = 20;
    // TURN_WAIT
    public static final int TURN_WAIT = 21;
    // TURN_OTHER_PLAYER
    public static final int TURN_OTHER_PLAYER = 22;
    // END_OF_ROUND (PRE-FLOP, FLOP, RIVER, ETC)
    public static final int END_OF_ROUND = 23;
    // END_OF_HAND (SHOW_DOWN)
    public static final int END_OF_HAND = 24;


    public static final int GAME_STARTS = 25;
    // END_OF_GAME
    public static final int END_OF_GAME = 26;
    


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
    // START GAME
    public static final int START_GAME = 72;
    // FULL TABLE
    public static final int FULL_TABLE = 73;
    // WAIT_GAME
    public static final int WAIT_GAME = 74;


    // SUITS
    public static final int HEARTS = 100;
    public static final int DIAMONDS = 101;
    public static final int CLUBS = 102;
    public static final int SPADES = 101;


    // NUMBERS
    public static final int NUMBER_ONE = 200;
    public static final int NUMBER_TWO = 201;
    public static final int NUMBER_THREE = 203;
    public static final int NUMBER_FOUR = 204;
    public static final int NUMBER_FIVE = 205;
    public static final int NUMBER_SIX = 206;
    public static final int NUMBER_SEVEN = 207;
    public static final int NUMBER_EIGHT = 208;
    public static final int NUMBER_NINE = 209;
    public static final int NUMBER_T = 210;
    public static final int NUMBER_J = 211;
    public static final int NUMBER_Q = 212;
    public static final int NUMBER_K = 213;
    public static final int NUMBER_A = 214;
    
}

 