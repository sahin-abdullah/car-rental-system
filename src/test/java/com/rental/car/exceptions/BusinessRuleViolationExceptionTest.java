package com.rental.car.exceptions;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BusinessRuleViolationExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        String message = "Invalid state transition";
        BusinessRuleViolationException exception = new BusinessRuleViolationException(message);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testExceptionCanBeThrown() {
        try {
            throw new BusinessRuleViolationException("Cannot complete reservation before return date");
        } catch (BusinessRuleViolationException e) {
            assertEquals(e.getMessage(), "Cannot complete reservation before return date");
        }
    }

    @Test
    public void testExceptionIsRuntimeException() {
        BusinessRuleViolationException exception = new BusinessRuleViolationException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
