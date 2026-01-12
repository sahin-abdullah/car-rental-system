package com.rental.car.inventory;

import com.rental.car.TestcontainersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.testng.Assert.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class InventoryIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private InventoryService inventoryService;

    private String testBranchCode;

    @BeforeMethod
    public void setUp() {
        testBranchCode = "LAX-TEST-" + System.currentTimeMillis();
        inventoryService.createBranchWithDetails(testBranchCode, "Test Branch", "+1-555-0100",
                "123 Test St", "Los Angeles", "CA", "USA", "90001");
    }

    @Test
    public void testCreateAndRetrieveBranch() {
        String branchCode = "SFO-TEST-" + System.currentTimeMillis();
        Branch branch = inventoryService.createBranchWithDetails(branchCode, "SFO Test", "+1-555-0200",
                "456 New St", "San Francisco", "CA", "USA", "94102");

        assertNotNull(branch);
        assertNotNull(branch.getId());
        assertEquals(branch.getCode(), branchCode);
    }

    @Test
    public void testCreateCar() {
        Car car = inventoryService.createCar(CarType.SEDAN, "TEST-" + System.currentTimeMillis(), 
                "Toyota", "Camry", 2024, testBranchCode, true);

        assertNotNull(car);
        assertTrue(car.isAvailable());
    }

    @Test
    public void testUpdateCarAvailability() {
        Car car = inventoryService.createCar(CarType.SEDAN, "AVAIL-" + System.currentTimeMillis(), 
                "Honda", "Accord", 2024, testBranchCode, true);

        inventoryService.updateCarAvailability(car.getId(), false);
        CarDTO updated = inventoryService.getCarById(car.getId()).orElseThrow();
        assertFalse(updated.available());
    }
}
