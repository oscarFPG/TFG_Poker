package com.ucm;

public class GameType {

    private GameType() {
    }

    // JUGADAS
    // FOLD
    public static final int FOLD = 1;
    // CHECK
    public static final int CHECK = 2;
    // ALL_IN
    public static final int ALL_IN = 3;
    // CALL
    public static final int CALL = 4;
    // RAISE
    public static final int RAISE = 5;

    // TURN_MOVE
    public static final int TURN_MOVE = 20;
    // TURN_WAIT
    public static final int TURN_WAIT = 21;
    // TURN_OTHER_PLAYER
    public static final int TURN_OTHER_PLAYER = 22;

    // MAX_TIME AVISA AL CLIENTE QUE SE HA QUEDADO SIN TIEMPO
    public static final int MAX_TIME = 40;
    // TURN_FINISH
    public static final int TURN_FINISH = 41;

    // GAME OVER WIN
    public static final int GAME_OVER_WIN = 60;
    // GAME OVER LOSE
    public static final int GAME_OVER_LOSE = 61;
    //

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
}
