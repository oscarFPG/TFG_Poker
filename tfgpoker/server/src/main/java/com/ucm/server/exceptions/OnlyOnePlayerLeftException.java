package com.ucm.server.exceptions;

/**
 * Exception thrown when a betting round ends with only one player remaining.
 * <p>
 * This typically occurs when all other players have folded.
 */
public class OnlyOnePlayerLeftException extends GameException {

     /**
     * Creates a new OnlyOnePlayerLeftException with a specific message.
     *
     * @param message a description of why the exception was thrown
     */
    public OnlyOnePlayerLeftException(String message) {
        super(message);
    }

}