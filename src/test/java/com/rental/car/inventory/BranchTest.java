package com.rental.car.inventory;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public class BranchTest {

    private Branch branch;
    private Address address;

    @BeforeMethod
    public void setUp() {
        address = new Address(1L, "123 Main St", null, 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch();
    }

    @Test
    public void testBranchCreation() {
        Branch b = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
        
        assertEquals(b.getId(), Long.valueOf(1L));
        assertEquals(b.getCode(), "LAX");
        assertEquals(b.getName(), "LAX Branch");
        assertEquals(b.getPhoneNumber(), "555-1234");
        assertNotNull(b.getAddress());
        assertEquals(b.getAddress().getCity(), "Los Angeles");
    }

    @Test
    public void testSettersAndGetters() {
        branch.setId(10L);
        branch.setCode("SFO");
        branch.setName("San Francisco Branch");
        branch.setPhoneNumber("555-5678");
        branch.setAddress(address);

        assertEquals(branch.getId(), Long.valueOf(10L));
        assertEquals(branch.getCode(), "SFO");
        assertEquals(branch.getName(), "San Francisco Branch");
        assertEquals(branch.getPhoneNumber(), "555-5678");
        assertNotNull(branch.getAddress());
    }

    @Test
    public void testNoArgsConstructor() {
        Branch b = new Branch();
        assertNotNull(b);
    }

    @Test
    public void testBranchCodeUniqueness() {
        Branch b1 = new Branch(1L, "NYC", "NYC Branch", "555-1111", address);
        Branch b2 = new Branch(2L, "NYC", "NYC Other Branch", "555-2222", address);
        
        assertEquals(b1.getCode(), b2.getCode());
    }

    @Test
    public void testAddressAssociation() {
        branch.setAddress(address);
        
        assertEquals(branch.getAddress().getStreet1(), "123 Main St");
        assertEquals(branch.getAddress().getCity(), "Los Angeles");
    }
}
