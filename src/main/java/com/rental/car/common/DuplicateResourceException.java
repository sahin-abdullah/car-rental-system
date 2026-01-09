package com.rental.car.common;

public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public static DuplicateResourceException carLicensePlate(String plate) {
        return new DuplicateResourceException("Car with license plate already exists: " + plate);
    }
    
    public static DuplicateResourceException branchCode(String code) {
        return new DuplicateResourceException("Branch with code already exists: " + code);
    }
}
