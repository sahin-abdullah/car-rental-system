package com.rental.car.common;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class ErrorResponseTest {

    @Test
    public void testErrorResponseCreation() {
        LocalDateTime now = LocalDateTime.now();
        List<String> details = Arrays.asList("Field error 1", "Field error 2");
        
        ErrorResponse error = new ErrorResponse(now, 400, "Bad Request", 
                "Invalid input", "/api/test", details);

        assertNotNull(error);
        assertEquals(error.timestamp(), now);
        assertEquals(error.status(), 400);
        assertEquals(error.error(), "Bad Request");
        assertEquals(error.message(), "Invalid input");
        assertEquals(error.path(), "/api/test");
        assertEquals(error.details(), details);
        assertEquals(error.details().size(), 2);
    }

    @Test
    public void testFactoryMethodWithoutDetails() {
        ErrorResponse error = ErrorResponse.of(404, "Not Found", 
                "Resource not found", "/api/cars/999");

        assertNotNull(error);
        assertEquals(error.status(), 404);
        assertEquals(error.error(), "Not Found");
        assertEquals(error.message(), "Resource not found");
        assertEquals(error.path(), "/api/cars/999");
        assertNull(error.details());
        assertNotNull(error.timestamp());
    }

    @Test
    public void testFactoryMethodWithDetails() {
        List<String> details = Arrays.asList("email: must not be null", 
                "name: size must be between 2 and 50");
        
        ErrorResponse error = ErrorResponse.of(400, "Bad Request", 
                "Validation failed", "/api/reservations", details);

        assertNotNull(error);
        assertEquals(error.status(), 400);
        assertEquals(error.error(), "Bad Request");
        assertEquals(error.message(), "Validation failed");
        assertEquals(error.path(), "/api/reservations");
        assertNotNull(error.details());
        assertEquals(error.details().size(), 2);
        assertNotNull(error.timestamp());
    }

    @Test
    public void testTimestampIsGenerated() {
        ErrorResponse error1 = ErrorResponse.of(500, "Internal Server Error", 
                "Server error", "/api/test");
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        ErrorResponse error2 = ErrorResponse.of(500, "Internal Server Error", 
                "Server error", "/api/test");

        assertNotNull(error1.timestamp());
        assertNotNull(error2.timestamp());
        assertTrue(error2.timestamp().isAfter(error1.timestamp()) || 
                   error2.timestamp().isEqual(error1.timestamp()));
    }

    @Test
    public void testErrorResponseEquality() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse error1 = new ErrorResponse(now, 404, "Not Found", 
                "Resource not found", "/api/test", null);
        ErrorResponse error2 = new ErrorResponse(now, 404, "Not Found", 
                "Resource not found", "/api/test", null);

        assertEquals(error1, error2);
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    public void testErrorResponseToString() {
        ErrorResponse error = ErrorResponse.of(409, "Conflict", 
                "Duplicate resource", "/api/branches");

        String toString = error.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("409"));
        assertTrue(toString.contains("Conflict"));
        assertTrue(toString.contains("Duplicate resource"));
    }

    @Test
    public void testNullDetails() {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 500, 
                "Internal Server Error", "Server error", "/api/test", null);

        assertNull(error.details());
    }
}
