package com.rental.car.inventory;

import com.rental.car.common.GeocodingService;
import com.rental.car.exceptions.DuplicateResourceException;
import com.rental.car.exceptions.ResourceNotFoundException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class InventoryServiceTest {

    @Mock
    private CarRepository carRepo;

    @Mock
    private BranchRepository branchRepo;

    @Mock
    private GeocodingService geoService;

    private InventoryService inventoryService;

    private Branch branch;
    private Address address;
    private Car car;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryService = new InventoryService(carRepo, branchRepo, geoService);
        
        address = new Address(1L, "123 Main St", null, 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
        car = new Car(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 2023, branch, true, 0);
    }

    @Test
    public void testIsValidBranch() {
        when(branchRepo.existsByCode("LAX")).thenReturn(true);
        
        boolean result = inventoryService.isValidBranch("LAX");
        
        assertTrue(result);
        verify(branchRepo, times(1)).existsByCode("LAX");
    }

    @Test
    public void testIsValidBranchNotFound() {
        when(branchRepo.existsByCode("INVALID")).thenReturn(false);
        
        boolean result = inventoryService.isValidBranch("INVALID");
        
        assertFalse(result);
    }

    @Test
    public void testGetAllBranches() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Branch> branches = Arrays.asList(branch);
        Page<Branch> page = new PageImpl<>(branches);
        
        when(branchRepo.findAll(pageable)).thenReturn(page);
        
        Page<Branch> result = inventoryService.getAllBranches(pageable);
        
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(branchRepo, times(1)).findAll(pageable);
    }

    @Test
    public void testGetBranchByCode() {
        when(branchRepo.findByCode("LAX")).thenReturn(Optional.of(branch));
        
        Optional<Branch> result = inventoryService.getBranchByCode("LAX");
        
        assertTrue(result.isPresent());
        assertEquals(result.get().getCode(), "LAX");
        verify(branchRepo, times(1)).findByCode("LAX");
    }

    @Test
    public void testGetCarById() {
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        
        Optional<CarDTO> result = inventoryService.getCarById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(result.get().id(), Long.valueOf(1L));
        assertEquals(result.get().licensePlate(), "ABC123");
        verify(carRepo, times(1)).findById(1L);
    }

    @Test
    public void testUpdateCarAvailability() {
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(carRepo.save(any(Car.class))).thenReturn(car);
        
        inventoryService.updateCarAvailability(1L, false);
        
        verify(carRepo, times(1)).findById(1L);
        verify(carRepo, times(1)).save(car);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testUpdateCarAvailabilityCarNotFound() {
        when(carRepo.findById(999L)).thenReturn(Optional.empty());
        
        inventoryService.updateCarAvailability(999L, false);
    }

    @Test
    public void testMoveCarToBranch() {
        Branch newBranch = new Branch(2L, "SFO", "SFO Branch", "555-5678", address);
        
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(branchRepo.findByCode("SFO")).thenReturn(Optional.of(newBranch));
        when(carRepo.save(any(Car.class))).thenReturn(car);
        
        inventoryService.moveCarToBranch(1L, "SFO");
        
        verify(carRepo, times(1)).findById(1L);
        verify(branchRepo, times(1)).findByCode("SFO");
        verify(carRepo, times(1)).save(car);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testMoveCarToBranchCarNotFound() {
        when(carRepo.findById(999L)).thenReturn(Optional.empty());
        
        inventoryService.moveCarToBranch(999L, "SFO");
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testMoveCarToBranchBranchNotFound() {
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(branchRepo.findByCode("INVALID")).thenReturn(Optional.empty());
        
        inventoryService.moveCarToBranch(1L, "INVALID");
    }

    @Test
    public void testCreateCar() {
        when(carRepo.existsByLicensePlate("NEW123")).thenReturn(false);
        when(branchRepo.findByCode("LAX")).thenReturn(Optional.of(branch));
        when(carRepo.save(any(Car.class))).thenReturn(car);
        
        Car result = inventoryService.createCar(CarType.SEDAN, "NEW123", "Toyota", "Camry", 2023, "LAX", true);
        
        assertNotNull(result);
        verify(carRepo, times(1)).existsByLicensePlate("NEW123");
        verify(branchRepo, times(1)).findByCode("LAX");
        verify(carRepo, times(1)).save(any(Car.class));
    }

    @Test(expectedExceptions = DuplicateResourceException.class)
    public void testCreateCarDuplicateLicensePlate() {
        when(carRepo.existsByLicensePlate("ABC123")).thenReturn(true);
        
        inventoryService.createCar(CarType.SEDAN, "ABC123", "Toyota", "Camry", 2023, "LAX", true);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateCarBranchNotFound() {
        when(carRepo.existsByLicensePlate("NEW123")).thenReturn(false);
        when(branchRepo.findByCode("INVALID")).thenReturn(Optional.empty());
        
        inventoryService.createCar(CarType.SEDAN, "NEW123", "Toyota", "Camry", 2023, "INVALID", true);
    }

    @Test
    public void testUpdateCar() {
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(carRepo.existsByLicensePlate("ABC123")).thenReturn(false);
        when(branchRepo.findByCode("LAX")).thenReturn(Optional.of(branch));
        when(carRepo.save(any(Car.class))).thenReturn(car);
        
        Car result = inventoryService.updateCar(1L, CarType.SUV, "ABC123", "Honda", "CR-V", 2022, "LAX", false);
        
        assertNotNull(result);
        verify(carRepo, times(1)).findById(1L);
        verify(carRepo, times(1)).save(any(Car.class));
    }

    @Test
    public void testDeleteCar() {
        when(carRepo.existsById(1L)).thenReturn(true);
        doNothing().when(carRepo).deleteById(1L);
        
        inventoryService.deleteCar(1L);
        
        verify(carRepo, times(1)).existsById(1L);
        verify(carRepo, times(1)).deleteById(1L);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testDeleteCarNotFound() {
        when(carRepo.existsById(999L)).thenReturn(false);
        
        inventoryService.deleteCar(999L);
    }

    @Test
    public void testCreateBranchWithDetails() {
        when(branchRepo.existsByCode("SFO")).thenReturn(false);
        when(geoService.getCoordinates(anyString())).thenReturn(new double[]{37.7749, -122.4194});
        when(branchRepo.save(any(Branch.class))).thenReturn(branch);
        
        Branch result = inventoryService.createBranchWithDetails("SFO", "SFO Branch", "555-9999", 
                "456 Oak St", "San Francisco", "CA", "USA", "94102");
        
        assertNotNull(result);
        verify(branchRepo, times(1)).existsByCode("SFO");
        verify(geoService, times(1)).getCoordinates(anyString());
        verify(branchRepo, times(1)).save(any(Branch.class));
    }

    @Test(expectedExceptions = DuplicateResourceException.class)
    public void testCreateBranchDuplicateCode() {
        when(branchRepo.existsByCode("LAX")).thenReturn(true);
        
        inventoryService.createBranchWithDetails("LAX", "LAX Branch", "555-1234", 
                "123 Main St", "Los Angeles", "CA", "USA", "90001");
    }

    @Test
    public void testUpdateBranch() {
        when(branchRepo.findByCode("LAX")).thenReturn(Optional.of(branch));
        when(geoService.getCoordinates(anyString())).thenReturn(new double[]{34.0522, -118.2437});
        when(branchRepo.save(any(Branch.class))).thenReturn(branch);
        
        Branch result = inventoryService.updateBranch("LAX", "Updated LAX", "555-1111", 
                "789 Pine St", "Los Angeles", "CA", "USA", "90002");
        
        assertNotNull(result);
        verify(branchRepo, times(1)).findByCode("LAX");
        verify(branchRepo, times(1)).save(any(Branch.class));
    }

    @Test
    public void testDeleteBranch() {
        when(branchRepo.findByCode("LAX")).thenReturn(Optional.of(branch));
        doNothing().when(branchRepo).delete(branch);
        
        inventoryService.deleteBranch("LAX");
        
        verify(branchRepo, times(1)).findByCode("LAX");
        verify(branchRepo, times(1)).delete(branch);
    }

    @Test
    public void testGetAvailableTypesAtBranch() {
        List<CarType> types = Arrays.asList(CarType.SEDAN, CarType.SUV);
        when(carRepo.findAvailableTypes("LAX")).thenReturn(types);
        
        List<CarType> result = inventoryService.getAvailableTypesAtBranch("LAX");
        
        assertNotNull(result);
        assertEquals(result.size(), 2);
        verify(carRepo, times(1)).findAvailableTypes("LAX");
    }

    @Test
    public void testGetAvailableMakesAtBranch() {
        List<String> makes = Arrays.asList("Toyota", "Honda");
        when(carRepo.findAvailableMakes("LAX", CarType.SEDAN)).thenReturn(makes);
        
        List<String> result = inventoryService.getAvailableMakesAtBranch("LAX", CarType.SEDAN);
        
        assertNotNull(result);
        assertEquals(result.size(), 2);
        verify(carRepo, times(1)).findAvailableMakes("LAX", CarType.SEDAN);
    }

    @Test
    public void testGetAvailableYearsAtBranch() {
        List<Integer> years = Arrays.asList(2023, 2022, 2021);
        when(carRepo.findAvailableYears("LAX", CarType.SEDAN, "Toyota")).thenReturn(years);
        
        List<Integer> result = inventoryService.getAvailableYearsAtBranch("LAX", CarType.SEDAN, "Toyota");
        
        assertNotNull(result);
        assertEquals(result.size(), 3);
        verify(carRepo, times(1)).findAvailableYears("LAX", CarType.SEDAN, "Toyota");
    }

    @Test
    public void testFindNearestBranches() {
        List<BranchWithDistance> branches = new ArrayList<>();
        when(branchRepo.findNearestBranches(34.0522, -118.2437, 5)).thenReturn(branches);
        
        List<BranchWithDistance> result = inventoryService.findNearestBranches(34.0522, -118.2437, 5);
        
        assertNotNull(result);
        verify(branchRepo, times(1)).findNearestBranches(34.0522, -118.2437, 5);
    }
}
