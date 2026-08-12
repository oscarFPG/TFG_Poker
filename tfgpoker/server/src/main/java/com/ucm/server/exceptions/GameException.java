package com.ucm.server.exceptions;

/**
 * Base exception class for errors that occur during the game execution.
 */
public class GameException extends Exception {

     /**
     * Creates a new GameException with a specific error message.
     * @param specificCause a description of the cause of the exception
     */
    public GameException(String specificCause){
        super(specificCause);
    }

}
