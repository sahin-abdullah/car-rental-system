package com.rental.car.exceptions;

/**
 * Exception thrown when a business rule is violated.
 * Examples: invalid state transitions, date validation failures, cancellation window violations.
 * Maps to HTTP 422 Unprocessable Entity
 */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
