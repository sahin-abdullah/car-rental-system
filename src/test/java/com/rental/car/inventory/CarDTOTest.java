package com.rental.car.inventory;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public class CarDTOTest {

    private Car car;
    private Branch branch;
    private Address address;

    @BeforeMethod
    public void setUp() {
        address = new Address(1L, "123 Main St", null, 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
        car = new Car(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 2023, branch, true, 0);
    }

    @Test
    public void testCarDTOCreation() {
        CarDTO dto = new CarDTO(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);
        
        assertEquals(dto.id(), Long.valueOf(1L));
        assertEquals(dto.type(), CarType.SEDAN);
        assertEquals(dto.licensePlate(), "ABC123");
        assertEquals(dto.make(), "Toyota");
        assertEquals(dto.model(), "Camry");
        assertEquals(dto.year(), 2023);
        assertEquals(dto.branchCode(), "LAX");
        assertEquals(dto.branchName(), "LAX Branch");
        assertEquals(dto.branchCity(), "Los Angeles");
        assertTrue(dto.available());
        assertNull(dto.distanceKm());
    }

    @Test
    public void testFromCar() {
        CarDTO dto = CarDTO.from(car);
        
        assertNotNull(dto);
        assertEquals(dto.id(), car.getId());
        assertEquals(dto.type(), car.getType());
        assertEquals(dto.licensePlate(), car.getLicensePlate());
        assertEquals(dto.make(), car.getMake());
        assertEquals(dto.model(), car.getModel());
        assertEquals(dto.year(), car.getYear());
        assertEquals(dto.branchCode(), branch.getCode());
        assertEquals(dto.branchName(), branch.getName());
        assertEquals(dto.branchCity(), address.getCity());
        assertEquals(dto.available(), car.isAvailable());
        assertNull(dto.distanceKm());
    }

    @Test
    public void testCarDTOWithDistance() {
        CarDTO dto = new CarDTO(1L, CarType.SUV, "XYZ789", "Honda", "CR-V", 
                2022, "SFO", "SFO Branch", "San Francisco", true, 50.5);
        
        assertNotNull(dto.distanceKm());
        assertEquals(dto.distanceKm(), 50.5, 0.001);
    }

    @Test
    public void testCarDTOUnavailable() {
        Car unavailableCar = new Car(2L, CarType.VAN, "VAN456", "Ford", "Transit", 
                2021, branch, false, 0);
        CarDTO dto = CarDTO.from(unavailableCar);
        
        assertFalse(dto.available());
    }

    @Test
    public void testRecordEquality() {
        CarDTO dto1 = new CarDTO(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);
        CarDTO dto2 = new CarDTO(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);
        
        assertEquals(dto1, dto2);
    }

    @Test
    public void testDifferentCarTypes() {
        CarDTO sedanDTO = new CarDTO(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);
        CarDTO suvDTO = new CarDTO(2L, CarType.SUV, "XYZ789", "Honda", "CR-V", 
                2022, "LAX", "LAX Branch", "Los Angeles", true, null);
        
        assertNotEquals(sedanDTO.type(), suvDTO.type());
    }
}
