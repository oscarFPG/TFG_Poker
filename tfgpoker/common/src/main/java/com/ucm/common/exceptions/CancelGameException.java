package com.ucm.common.exceptions;

public class CancelGameException extends Exception {
    
    public CancelGameException() {
        super("The game has been cancelled");
    }
    
    public CancelGameException(final String msg) {
        super(msg);
    }
}
