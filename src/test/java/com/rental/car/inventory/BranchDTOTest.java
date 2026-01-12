package com.rental.car.inventory;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public class BranchDTOTest {

    private Branch branch;
    private Address address;

    @BeforeMethod
    public void setUp() {
        address = new Address(1L, "123 Main St", "Suite 100", 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
    }

    @Test
    public void testBranchDTOCreation() {
        BranchDTO dto = new BranchDTO(1L, "LAX", "LAX Branch", "555-1234", 
                "123 Main St", "Los Angeles", "CA", "USA", "90001", 34.0522, -118.2437);
        
        assertEquals(dto.id(), Long.valueOf(1L));
        assertEquals(dto.code(), "LAX");
        assertEquals(dto.name(), "LAX Branch");
        assertEquals(dto.phoneNumber(), "555-1234");
        assertEquals(dto.street(), "123 Main St");
        assertEquals(dto.city(), "Los Angeles");
        assertEquals(dto.state(), "CA");
        assertEquals(dto.country(), "USA");
        assertEquals(dto.zipCode(), "90001");
        assertEquals(dto.latitude(), 34.0522, 0.0001);
        assertEquals(dto.longitude(), -118.2437, 0.0001);
    }

    @Test
    public void testFromBranch() {
        BranchDTO dto = BranchDTO.from(branch);
        
        assertNotNull(dto);
        assertEquals(dto.id(), branch.getId());
        assertEquals(dto.code(), branch.getCode());
        assertEquals(dto.name(), branch.getName());
        assertEquals(dto.phoneNumber(), branch.getPhoneNumber());
        assertEquals(dto.street(), address.getStreet1());
        assertEquals(dto.city(), address.getCity());
        assertEquals(dto.state(), address.getState());
        assertEquals(dto.country(), address.getCountry());
        assertEquals(dto.zipCode(), address.getZipCode());
        assertEquals(dto.latitude(), address.getLatitude(), 0.0001);
        assertEquals(dto.longitude(), address.getLongitude(), 0.0001);
    }

    @Test
    public void testFromBranchWithDifferentData() {
        Address newAddress = new Address(2L, "456 Oak St", null, 
                "San Francisco", "CA", "94102", "USA", 37.7749, -122.4194);
        Branch newBranch = new Branch(2L, "SFO", "SFO Branch", "555-9999", newAddress);
        
        BranchDTO dto = BranchDTO.from(newBranch);
        
        assertEquals(dto.id(), Long.valueOf(2L));
        assertEquals(dto.code(), "SFO");
        assertEquals(dto.city(), "San Francisco");
        assertEquals(dto.latitude(), 37.7749, 0.0001);
    }

    @Test
    public void testRecordEquality() {
        BranchDTO dto1 = new BranchDTO(1L, "LAX", "LAX Branch", "555-1234", 
                "123 Main St", "Los Angeles", "CA", "USA", "90001", 34.0522, -118.2437);
        BranchDTO dto2 = new BranchDTO(1L, "LAX", "LAX Branch", "555-1234", 
                "123 Main St", "Los Angeles", "CA", "USA", "90001", 34.0522, -118.2437);
        
        assertEquals(dto1, dto2);
    }
}
