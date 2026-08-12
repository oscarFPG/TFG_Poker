package com.ucm.server.exceptions;

/**
 * Exception thrown when an error occurs during the evaluation of a poker hand.
 */
public class EvaluatorException extends GameException {

    /**
     * Constructs a new EvaluatorException with the specified detail message.
     * @param message the detail message
     */
    public EvaluatorException(String message) {
        super(message);
    }
}