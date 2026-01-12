package com.rental.car.inventory;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public class AddressTest {

    private Address address;

    @BeforeMethod
    public void setUp() {
        address = new Address();
    }

    @Test
    public void testAddressCreation() {
        Address addr = new Address(1L, "123 Main St", "Apt 4B", 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        
        assertEquals(addr.getId(), Long.valueOf(1L));
        assertEquals(addr.getStreet1(), "123 Main St");
        assertEquals(addr.getStreet2(), "Apt 4B");
        assertEquals(addr.getCity(), "Los Angeles");
        assertEquals(addr.getState(), "CA");
        assertEquals(addr.getZipCode(), "90001");
        assertEquals(addr.getCountry(), "USA");
        assertEquals(addr.getLatitude(), 34.0522, 0.0001);
        assertEquals(addr.getLongitude(), -118.2437, 0.0001);
    }

    @Test
    public void testSettersAndGetters() {
        address.setId(100L);
        address.setStreet1("456 Oak Ave");
        address.setStreet2("Suite 200");
        address.setCity("San Francisco");
        address.setState("CA");
        address.setZipCode("94102");
        address.setCountry("USA");
        address.setLatitude(37.7749);
        address.setLongitude(-122.4194);

        assertEquals(address.getId(), Long.valueOf(100L));
        assertEquals(address.getStreet1(), "456 Oak Ave");
        assertEquals(address.getStreet2(), "Suite 200");
        assertEquals(address.getCity(), "San Francisco");
        assertEquals(address.getState(), "CA");
        assertEquals(address.getZipCode(), "94102");
        assertEquals(address.getCountry(), "USA");
        assertEquals(address.getLatitude(), 37.7749, 0.0001);
        assertEquals(address.getLongitude(), -122.4194, 0.0001);
    }

    @Test
    public void testNoArgsConstructor() {
        Address addr = new Address();
        assertNotNull(addr);
    }

    @Test
    public void testCoordinatesSetCorrectly() {
        address.setLatitude(40.7128);
        address.setLongitude(-74.0060);
        
        assertEquals(address.getLatitude(), 40.7128);
        assertEquals(address.getLongitude(), -74.0060);
    }

    @Test
    public void testAddressWithNullStreet2() {
        Address addr = new Address(1L, "789 Pine St", null, 
                "Seattle", "WA", "98101", "USA", 47.6062, -122.3321);
        
        assertNull(addr.getStreet2());
    }
}
