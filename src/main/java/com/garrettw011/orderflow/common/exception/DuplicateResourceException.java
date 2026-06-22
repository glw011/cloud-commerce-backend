package com.garrettw011.orderflow.common.exception;

/**
 * Http status: 409
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) { super(message); }
}