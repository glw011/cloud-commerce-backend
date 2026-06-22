package com.garrettw011.orderflow.common.exception;

/**
 * HTTP status: 404
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}