package com.rental.car.exceptions;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ResourceNotFoundExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testCarFactory() {
        ResourceNotFoundException exception = ResourceNotFoundException.car(123L);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Car not found: 123");
    }

    @Test
    public void testBranchFactory() {
        ResourceNotFoundException exception = ResourceNotFoundException.branch("LAX");

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Branch not found: LAX");
    }

    @Test
    public void testReservationFactory() {
        ResourceNotFoundException exception = ResourceNotFoundException.reservation(456L);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Reservation not found: 456");
    }

    @Test
    public void testExceptionCanBeThrown() {
        try {
            throw ResourceNotFoundException.car(999L);
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("999"));
        }
    }

    @Test
    public void testIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
