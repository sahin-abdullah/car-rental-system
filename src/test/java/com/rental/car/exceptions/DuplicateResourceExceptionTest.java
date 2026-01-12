package com.rental.car.exceptions;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DuplicateResourceExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        String message = "Resource already exists";
        DuplicateResourceException exception = new DuplicateResourceException(message);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testCarLicensePlateFactory() {
        DuplicateResourceException exception = DuplicateResourceException.carLicensePlate("ABC123");

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Car with license plate already exists: ABC123");
    }

    @Test
    public void testBranchCodeFactory() {
        DuplicateResourceException exception = DuplicateResourceException.branchCode("LAX");

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "Branch with code already exists: LAX");
    }

    @Test
    public void testExceptionCanBeThrown() {
        try {
            throw DuplicateResourceException.carLicensePlate("XYZ789");
        } catch (DuplicateResourceException e) {
            assertTrue(e.getMessage().contains("XYZ789"));
        }
    }

    @Test
    public void testIsRuntimeException() {
        DuplicateResourceException exception = new DuplicateResourceException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
