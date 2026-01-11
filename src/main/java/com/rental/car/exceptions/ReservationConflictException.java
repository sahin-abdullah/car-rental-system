package com.rental.car.exceptions;

/**
 * Exception thrown when a reservation conflicts with existing reservations
 * or business rules (e.g., double booking, invalid dates)
 */
public class ReservationConflictException extends RuntimeException {
    
    public ReservationConflictException(String message) {
        super(message);
    }
    
    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
