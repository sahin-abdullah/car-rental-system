package com.rental.car.common;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public static ResourceNotFoundException car(Long id) {
        return new ResourceNotFoundException("Car not found: " + id);
    }
    
    public static ResourceNotFoundException branch(String code) {
        return new ResourceNotFoundException("Branch not found: " + code);
    }
}
