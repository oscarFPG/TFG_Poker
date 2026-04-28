package com.ucm.server.exceptions;

import java.io.IOException;

public class TurnTimeoutException extends IOException {
    public TurnTimeoutException() {
        super("Player turn timeout");
    }
}
