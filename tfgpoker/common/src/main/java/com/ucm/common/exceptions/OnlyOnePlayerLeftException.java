package com.ucm.common.exceptions;


/**
 * Exception thrown when a betting round ends with only one player remaining.
 * <p>
 * This typically occurs when all other players have folded.
 */
public class OnlyOnePlayerLeftException extends Exception {
 
    /**
    * Exception thrown when there is only one player actaive during a hand
    */
    public OnlyOnePlayerLeftException() {
        super("There is only one player left on the current hand");
    }    

}