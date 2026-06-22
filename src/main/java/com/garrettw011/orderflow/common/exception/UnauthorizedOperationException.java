package com.garrettw011.orderflow.common.exception;

/**
 * Http status: 403
 */
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) { super(message); }
}
