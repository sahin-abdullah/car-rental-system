package com.rental.car.inventory;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public class CarTest {

    private Car car;
    private Branch branch;
    private Address address;

    @BeforeMethod
    public void setUp() {
        address = new Address(1L, "123 Main St", null, 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
        car = new Car();
    }

    @Test
    public void testCarCreation() {
        Car c = new Car(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 2023, branch, true, 0);
        
        assertEquals(c.getId(), Long.valueOf(1L));
        assertEquals(c.getType(), CarType.SEDAN);
        assertEquals(c.getLicensePlate(), "ABC123");
        assertEquals(c.getMake(), "Toyota");
        assertEquals(c.getModel(), "Camry");
        assertEquals(c.getYear(), 2023);
        assertTrue(c.isAvailable());
        assertNotNull(c.getCurrentBranch());
        assertEquals(c.getVersion(), Integer.valueOf(0));
    }

    @Test
    public void testSettersAndGetters() {
        car.setId(10L);
        car.setType(CarType.SUV);
        car.setLicensePlate("XYZ789");
        car.setMake("Honda");
        car.setModel("CR-V");
        car.setYear(2022);
        car.setCurrentBranch(branch);
        car.setAvailable(false);
        car.setVersion(1);

        assertEquals(car.getId(), Long.valueOf(10L));
        assertEquals(car.getType(), CarType.SUV);
        assertEquals(car.getLicensePlate(), "XYZ789");
        assertEquals(car.getMake(), "Honda");
        assertEquals(car.getModel(), "CR-V");
        assertEquals(car.getYear(), 2022);
        assertFalse(car.isAvailable());
        assertNotNull(car.getCurrentBranch());
        assertEquals(car.getVersion(), Integer.valueOf(1));
    }

    @Test
    public void testNoArgsConstructor() {
        Car c = new Car();
        assertNotNull(c);
    }

    @Test
    public void testDefaultAvailability() {
        Car c = new Car();
        c.setAvailable(true);
        assertTrue(c.isAvailable());
    }

    @Test
    public void testCarTypeEnum() {
        car.setType(CarType.VAN);
        assertEquals(car.getType(), CarType.VAN);
    }

    @Test
    public void testBranchAssociation() {
        car.setCurrentBranch(branch);
        
        assertEquals(car.getCurrentBranch().getCode(), "LAX");
        assertEquals(car.getCurrentBranch().getName(), "LAX Branch");
    }

    @Test
    public void testVersioning() {
        car.setVersion(5);
        assertEquals(car.getVersion(), Integer.valueOf(5));
    }

    @Test
    public void testLicensePlatUniqueness() {
        Car c1 = new Car(1L, CarType.SEDAN, "SAME123", "Toyota", "Camry", 2023, branch, true, 0);
        Car c2 = new Car(2L, CarType.SUV, "SAME123", "Honda", "CR-V", 2022, branch, true, 0);
        
        assertEquals(c1.getLicensePlate(), c2.getLicensePlate());
    }
}
