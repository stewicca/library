package com.library.api.exception;

/**
 * Thrown when a requested domain resource (item, member, loan) does not exist.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 *
 * @author stewicca
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
