package com.rental.car.inventory;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class InventoryControllerTest {

    @Mock
    private InventoryService service;

    private InventorySearchController controller;

    private CarDTO carDTO;
    private Branch branch;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new InventorySearchController(service);
        
        carDTO = new CarDTO(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);
        
        Address address = new Address(1L, "123 Main St", null, 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
    }

    @Test
    public void testSearchFleet() {
        Page<CarDTO> page = new PageImpl<>(Arrays.asList(carDTO));
        when(service.searchWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);
        
        Page<CarDTO> result = controller.searchFleet(null, "LAX", CarType.SEDAN, "Toyota", 2023, 
                null, null, 0, 10, "id", "asc");
        
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(service, times(1)).searchWithFilters(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSearchFleetInvalidDateRange() {
        LocalDate pickupDate = LocalDate.of(2024, 1, 15);
        LocalDate returnDate = LocalDate.of(2024, 1, 10);
        
        controller.searchFleet(null, "LAX", null, null, null, 
                pickupDate, returnDate, 0, 10, "id", "asc");
    }

    @Test
    public void testGetCarById() {
        when(service.getCarById(1L)).thenReturn(Optional.of(carDTO));
        
        ResponseEntity<CarDTO> result = controller.getCarById(1L);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        assertEquals(result.getBody().id(), Long.valueOf(1L));
        verify(service, times(1)).getCarById(1L);
    }

    @Test
    public void testGetCarByIdNotFound() {
        when(service.getCarById(999L)).thenReturn(Optional.empty());
        
        ResponseEntity<CarDTO> result = controller.getCarById(999L);
        
        assertTrue(result.getStatusCode().is4xxClientError());
        assertNull(result.getBody());
    }

    @Test
    public void testGetAllBranches() {
        Page<Branch> page = new PageImpl<>(Arrays.asList(branch));
        when(service.getAllBranches(any(Pageable.class))).thenReturn(page);
        
        Page<BranchDTO> result = controller.getAllBranches(0, 10);
        
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(service, times(1)).getAllBranches(any(Pageable.class));
    }

    @Test
    public void testGetBranchByCode() {
        when(service.getBranchByCode("LAX")).thenReturn(Optional.of(branch));
        
        ResponseEntity<BranchDTO> result = controller.getBranchByCode("LAX");
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        assertEquals(result.getBody().code(), "LAX");
        verify(service, times(1)).getBranchByCode("LAX");
    }

    @Test
    public void testGetBranchByCodeNotFound() {
        when(service.getBranchByCode("INVALID")).thenReturn(Optional.empty());
        
        ResponseEntity<BranchDTO> result = controller.getBranchByCode("INVALID");
        
        assertTrue(result.getStatusCode().is4xxClientError());
        assertNull(result.getBody());
    }

    @Test
    public void testGetNearestBranches() {
        List<BranchWithDistance> branches = Arrays.asList();
        when(service.findNearestBranches(34.0522, -118.2437, 5)).thenReturn(branches);
        
        List<BranchWithDistance> result = controller.getNearestBranches(34.0522, -118.2437);
        
        assertNotNull(result);
        verify(service, times(1)).findNearestBranches(34.0522, -118.2437, 5);
    }

    @Test
    public void testGetBranchFiltersTypes() {
        List<CarType> types = Arrays.asList(CarType.SEDAN, CarType.SUV);
        when(service.getAvailableTypesAtBranch("LAX")).thenReturn(types);
        
        ResponseEntity<?> result = controller.getBranchFilters("LAX", "types", null, null);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).getAvailableTypesAtBranch("LAX");
    }

    @Test
    public void testGetBranchFiltersMakes() {
        List<String> makes = Arrays.asList("Toyota", "Honda");
        when(service.getAvailableMakesAtBranch("LAX", CarType.SEDAN)).thenReturn(makes);
        
        ResponseEntity<?> result = controller.getBranchFilters("LAX", "makes", CarType.SEDAN, null);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).getAvailableMakesAtBranch("LAX", CarType.SEDAN);
    }

    @Test
    public void testGetBranchFiltersYears() {
        List<Integer> years = Arrays.asList(2023, 2022);
        when(service.getAvailableYearsAtBranch("LAX", CarType.SEDAN, "Toyota")).thenReturn(years);
        
        ResponseEntity<?> result = controller.getBranchFilters("LAX", "years", CarType.SEDAN, "Toyota");
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).getAvailableYearsAtBranch("LAX", CarType.SEDAN, "Toyota");
    }

    @Test
    public void testGetBranchFiltersInvalid() {
        ResponseEntity<?> result = controller.getBranchFilters("LAX", "invalid", null, null);
        
        assertTrue(result.getStatusCode().is4xxClientError());
    }
}
