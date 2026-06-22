package com.garrettw011.orderflow.common.exception;

/**
 * Http status: 409
 */
public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) { super(message); }
}
