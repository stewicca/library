package com.library.api.exception;

/**
 * Thrown when a domain rule is violated (e.g. borrowing an item with no copies left,
 * or registering a duplicate member number). Mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 *
 * @author stewicca
 * @version 1.0
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
