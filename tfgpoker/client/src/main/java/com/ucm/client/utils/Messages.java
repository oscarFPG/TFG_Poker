package com.ucm.client.utils;

public final class Messages {

    private Messages() {}

     public static final String GLOBAL_CSS_PATH = "/original/css/style.css";

    // ---------------------- ALERTS -----------------------------
    public static final class Alerts {

        public static final String NOT_ENOUGH_PLAYERS_TITLE = "Cannot start game";
        public static final String NOT_ENOUGH_PLAYERS_MSG = "Not enough players in the table. You must add at least one bot or one player before starting the game";
    }

    // ------------------------- CONFIRM -------------------------------------
    public static final class Confirm {

        public static final String EXIT_TITLE = "Exit application";
        public static final String EXIT_MSG =
                "Are you sure you want to exit?";
    }

    // ----------------NOTIFICATIONS ------------------------
    public static final class Notifications {

        //ERRORS
        public static final String ERROR_NAME_ALREADY_USED = "Name already used. You must add a name that has not already register";
        public static final String ERROR_NAME_TOO_SHORT = "Name is too short!";
        public static final String ERROR_NAME_TOO_LONG = "Name is too long!";
        public static final String ERROR_CONNECTING_TO_SOCKET= "Error connecting to the socket: ";
        public static final String ERROR_GAME_NOT_CREATED = "Error creating game!";
        public static final String ERROR_CLIENT_TYPE = "ClientType unknown";
        public static final String ERROR_CLIENT_SOCKET = "Error on client socket: ";
        public static final String ERROR_GAME_CANCELED_BY_SERVER = "Game has been cancelled by the server!";
        public static final String UNKNOWN_SERVER_ERROR = "Unknown server response: ";
        public static final String ERROR_SENDING_EVENT_GAME_STARTS = "Error sending EVENT_GAME_STARTS to server: ";
        public static final String ERROR_UNKNOWN_CODE = "Unknown code: ";
        public static final String ERROR_UNKNOWN_TURN_CODE = "Unknown turn code: ";
        public static final String ERROR_SERVER_ROOM_NAME = "Error server room name: ";
        public static final String ERROR_SOCKET_DEVELOP = "Minor problem with socket ONLY for development: ";
        public static final String ERROR_GAME_NOT_JOINED = "Client cannot join!";
        public static final String ERROR_RECEIVING_RESPONSE_SERVER = "Error receiving response from server: ";
        public static final String ERROR_CLOSING_SOCKET = "Error closing socket: ";
        public static final String ERROR_MISSING_PLAYERS = "Game cannot start! Missing players";
        public static final String ERROR_ONLY_ONE_PLAYER_LEFT = "There is only one player left!";
        public static final String ERROR_RECEIVING_RANK = "Error receiving the rank after a fold exception: ";
        public static final String ERROR_THREAD_INTERRUPTED = "Game thread interrupted: ";
        public static final String ERROR_UNKNOWN_CODE_SHOWDOWN = "Unknown code %d in showdown!";
        public static final String ERROR_COMMAND_NOT_VALID = "Command %s not valid! Try again";
        public static final String ERROR_SENDING_COMMAND = "Error sending the command: ";
        public static final String ERROR_UNKNOWN_EVENT = "Event %d unknown!";
        public static final String ERROR_NOT_IN_LIST = "We are not in the list! Something is wrong...";
        
        //CONFIRMATIONS
        public static final String CONFIRMATION_NAME_VALID = "Welcome ";
        public static final String CONFIRMATION_WAITING_GAME = "All correct! Creating room...";
        public static final String CONFIRMATION_WINNER_GAME = "Player %s is the winner of the hand";
        public static final String CONFIRMATION_PLAYER_ELIMINATED = "Player %s has been eliminated from the game!";
        public static final String CONFIRMATION_GAME_FINISHED = "All OK! Game finished!";
        public static final String CONFIRMATION_GAME_STARTS = "Game has to start!";
        public static final String CONFIRMATION_ROUND_ENDS = "Round has ended!";

        //INFO
        public static final String PLAYER_ID_IS = "Player ID is ";
        public static final String PLAYER_IS_HOST = "This client is the host of the game!";
        public static final String PLAYER_IS_GUEST = "This client is guest!";
        public static final String CHECK_ACTION_FULL = "CHECK BUTTON";
        public static final String CALL_ACTION_FULL = "CALL BUTTON";
        public static final String RAISE_ACTION_FULL = "RAISE BUTTON: ";
        public static final String TURN_FORCED_SB = "Forced play as the small blind with chips: ";
        public static final String TURN_FORCED_BB = "Forced play as the big blind with chips: ";
        public static final String TURN_PLAY = "It's your turn to play!";
        public static final String WAITING_GAME_START = "Waiting for the game to start...";

      
    }
}
