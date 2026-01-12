package com.rental.car.exceptions;

import com.rental.car.common.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    private GlobalExceptionHandler handler;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    public void testHandleResourceNotFound() {
        ResourceNotFoundException exception = ResourceNotFoundException.car(123L);

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 404);
        assertEquals(response.getBody().message(), "Car not found: 123");
        assertEquals(response.getBody().path(), "/api/test");
    }

    @Test
    public void testHandleDuplicateResource() {
        DuplicateResourceException exception = DuplicateResourceException.carLicensePlate("ABC123");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateResource(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 409);
        assertTrue(response.getBody().message().contains("ABC123"));
    }

    @Test
    public void testHandleExternalService() {
        ExternalServiceException exception = new ExternalServiceException("API unavailable");

        ResponseEntity<ErrorResponse> response = handler.handleExternalService(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 502);
        assertEquals(response.getBody().message(), "API unavailable");
    }

    @Test
    public void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 409);
        assertEquals(response.getBody().message(), "Resource cannot be deleted due to existing references");
    }

    @Test
    public void testHandleValidationErrors() {
        FieldError fieldError1 = new FieldError("request", "email", "must not be null");
        FieldError fieldError2 = new FieldError("request", "name", "size must be between 2 and 50");
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 400);
        assertEquals(response.getBody().message(), "Validation failed");
        assertNotNull(response.getBody().details());
        assertEquals(response.getBody().details().size(), 2);
    }

    @Test
    public void testHandleReservationConflict() {
        ReservationConflictException exception = new ReservationConflictException("Car already booked");

        ResponseEntity<ErrorResponse> response = handler.handleReservationConflict(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 409);
        assertEquals(response.getBody().message(), "Car already booked");
    }

    @Test
    public void testHandleBusinessRuleViolation() {
        BusinessRuleViolationException exception = new BusinessRuleViolationException("Invalid state transition");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessRuleViolation(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 422);
        assertEquals(response.getBody().message(), "Invalid state transition");
    }

    @Test
    public void testHandleIllegalState() {
        IllegalStateException exception = new IllegalStateException("Invalid state");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 422);
        assertEquals(response.getBody().message(), "Invalid state");
    }

    @Test
    public void testHandleIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid parameter");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 400);
        assertEquals(response.getBody().message(), "Invalid parameter");
    }

    @Test
    public void testHandleOptimisticLockException() {
        OptimisticLockException exception = new OptimisticLockException("Version conflict");

        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLock(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 409);
        assertTrue(response.getBody().message().contains("modified by another user"));
    }

    @Test
    public void testHandleObjectOptimisticLockingFailure() {
        ObjectOptimisticLockingFailureException exception = 
            new ObjectOptimisticLockingFailureException("Entity", "id");

        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLock(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 409);
    }

    @Test
    public void testHandleGenericException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().status(), 500);
        assertEquals(response.getBody().message(), "Unexpected error");
    }

    @Test
    public void testHandleGenericExceptionWithNullMessage() {
        Exception exception = new Exception((String) null);

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception, request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().message(), "An unexpected error occurred");
    }
}
