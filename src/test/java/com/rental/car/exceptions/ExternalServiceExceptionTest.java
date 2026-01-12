package com.rental.car.exceptions;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ExternalServiceExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        String message = "External API call failed";
        ExternalServiceException exception = new ExternalServiceException(message);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testExceptionWithMessageAndCause() {
        String message = "Geocoding service unavailable";
        Throwable cause = new RuntimeException("Connection timeout");
        ExternalServiceException exception = new ExternalServiceException(message, cause);

        assertNotNull(exception);
        assertEquals(exception.getMessage(), message);
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause(), cause);
    }

    @Test
    public void testExceptionCanBeThrown() {
        try {
            throw new ExternalServiceException("Service error", new Exception("Network failure"));
        } catch (ExternalServiceException e) {
            assertEquals(e.getMessage(), "Service error");
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void testIsRuntimeException() {
        ExternalServiceException exception = new ExternalServiceException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
