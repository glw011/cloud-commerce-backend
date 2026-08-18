package com.garrettw011.orderflow.common.exception;

/**
 * Http status: 409
 */
public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String message) { super(message); }
}
