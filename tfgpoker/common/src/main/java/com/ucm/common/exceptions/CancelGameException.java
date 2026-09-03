package com.ucm.common.exceptions;

/**
 * Class that represents an exception thrown when a game is cancelled.
 */
public class CancelGameException extends Exception {
    
    /**
     * Constructor for the CancelGameException class.
     */
    public CancelGameException() {
        super("The game has been cancelled");
    }
    /**
     * Constructor for the CancelGameException class with a custom message.
     * @param msg
     */
    public CancelGameException(final String msg) {
        super(msg);
    }
}
