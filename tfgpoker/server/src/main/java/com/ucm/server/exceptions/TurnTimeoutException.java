package com.ucm.server.exceptions;

import java.io.IOException;

/**
 * Exception thrown when a player's turn times out.
 */
public class TurnTimeoutException extends IOException {

    /**
     * Constructs a new TurnTimeoutException with a default message indicating that the player's turn has timed out.
     */
    public TurnTimeoutException() {
        super("Player turn timeout");
    }

}
