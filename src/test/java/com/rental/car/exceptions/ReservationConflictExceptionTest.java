package com.rental.car.exceptions;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ReservationConflictExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        String message = "Car is already reserved for the selected dates";
        ReservationConflictException exception = new ReservationConflictException(message);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testExceptionWithMessageAndCause() {
        String message = "Conflicting reservation detected";
        Throwable cause = new RuntimeException("Database constraint violation");
        ReservationConflictException exception = new ReservationConflictException(message, cause);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause(), cause);
    }

    @Test
    public void testExceptionCanBeThrown() {
        try {
            throw new ReservationConflictException("Double booking detected");
        } catch (ReservationConflictException e) {
            assertEquals(e.getMessage(), "Double booking detected");
        }
    }

    @Test
    public void testIsRuntimeException() {
        ReservationConflictException exception = new ReservationConflictException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
