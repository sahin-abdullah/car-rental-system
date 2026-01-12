package com.rental.car.inventory;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class InventoryInternalControllerTest {

    @Mock
    private InventoryService service;

    private InventoryInternalController controller;

    private Car car;
    private Branch branch;
    private Address address;
    private CarDTO carDTO;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new InventoryInternalController(service);
        
        address = new Address(1L, "123 Main St", null, 
                "Los Angeles", "CA", "90001", "USA", 34.0522, -118.2437);
        branch = new Branch(1L, "LAX", "LAX Branch", "555-1234", address);
        car = new Car(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 2023, branch, true, 0);
        carDTO = new CarDTO(1L, CarType.SEDAN, "ABC123", "Toyota", "Camry", 
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);
    }

    @Test
    public void testCheckBranchExists() {
        when(service.isValidBranch("LAX")).thenReturn(true);
        
        boolean result = controller.checkBranchExists("LAX");
        
        assertTrue(result);
        verify(service, times(1)).isValidBranch("LAX");
    }

    @Test
    public void testCheckBranchExistsFalse() {
        when(service.isValidBranch("INVALID")).thenReturn(false);
        
        boolean result = controller.checkBranchExists("INVALID");
        
        assertFalse(result);
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
    public void testGetCarById() {
        when(service.getCarById(1L)).thenReturn(Optional.of(carDTO));
        
        ResponseEntity<CarDTO> result = controller.getCarById(1L);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).getCarById(1L);
    }

    @Test
    public void testUpdateCarAvailability() {
        doNothing().when(service).updateCarAvailability(1L, false);
        InventoryInternalController.AvailabilityUpdateRequest request = 
                new InventoryInternalController.AvailabilityUpdateRequest(false);
        
        ResponseEntity<Void> result = controller.updateCarAvailability(1L, request);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).updateCarAvailability(1L, false);
    }

    @Test
    public void testMoveCarToBranch() {
        doNothing().when(service).moveCarToBranch(1L, "SFO");
        InventoryInternalController.BranchTransferRequest request = 
                new InventoryInternalController.BranchTransferRequest("SFO");
        
        ResponseEntity<Void> result = controller.moveCarToBranch(1L, request);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).moveCarToBranch(1L, "SFO");
    }

    @Test
    public void testCreateCar() {
        when(service.createCar(any(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(car);
        
        InventoryInternalController.CarCreateRequest request = 
                new InventoryInternalController.CarCreateRequest(CarType.SEDAN, "NEW123", "Toyota", 
                        "Camry", 2023, "LAX", true);
        
        ResponseEntity<CarDTO> result = controller.createCar(request);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).createCar(any(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyBoolean());
    }

    @Test
    public void testUpdateCar() {
        when(service.updateCar(anyLong(), any(), anyString(), anyString(), anyString(), anyInt(), anyString(), any()))
                .thenReturn(car);
        
        InventoryInternalController.CarUpdateRequest request = 
                new InventoryInternalController.CarUpdateRequest(CarType.SUV, "ABC123", "Honda", 
                        "CR-V", 2022, "LAX", false);
        
        ResponseEntity<CarDTO> result = controller.updateCar(1L, request);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).updateCar(anyLong(), any(), anyString(), anyString(), anyString(), anyInt(), anyString(), any());
    }

    @Test
    public void testDeleteCar() {
        doNothing().when(service).deleteCar(1L);
        
        ResponseEntity<Void> result = controller.deleteCar(1L);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).deleteCar(1L);
    }

    @Test
    public void testCreateBranch() {
        when(service.createBranchWithDetails(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString())).thenReturn(branch);
        
        InventoryInternalController.BranchCreateRequest request = 
                new InventoryInternalController.BranchCreateRequest("SFO", "SFO Branch", "555-9999", 
                        "456 Oak St", "San Francisco", "CA", "USA", "94102");
        
        ResponseEntity<BranchDTO> result = controller.createBranch(request);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).createBranchWithDetails(anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateBranch() {
        when(service.updateBranch(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString())).thenReturn(branch);
        
        InventoryInternalController.BranchUpdateRequest request = 
                new InventoryInternalController.BranchUpdateRequest("Updated LAX", "555-1111", 
                        "789 Pine St", "Los Angeles", "CA", "USA", "90002");
        
        ResponseEntity<BranchDTO> result = controller.updateBranch("LAX", request);
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).updateBranch(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testDeleteBranch() {
        doNothing().when(service).deleteBranch("LAX");
        
        ResponseEntity<Void> result = controller.deleteBranch("LAX");
        
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(service, times(1)).deleteBranch("LAX");
    }

    @Test
    public void testCarCreateRequestRecord() {
        InventoryInternalController.CarCreateRequest request = 
                new InventoryInternalController.CarCreateRequest(CarType.SEDAN, "ABC123", "Toyota", 
                        "Camry", 2023, "LAX", true);
        
        assertEquals(request.type(), CarType.SEDAN);
        assertEquals(request.licensePlate(), "ABC123");
        assertEquals(request.make(), "Toyota");
        assertEquals(request.model(), "Camry");
        assertEquals(request.year(), 2023);
        assertEquals(request.branchCode(), "LAX");
        assertTrue(request.available());
    }

    @Test
    public void testBranchTransferRequestRecord() {
        InventoryInternalController.BranchTransferRequest request = 
                new InventoryInternalController.BranchTransferRequest("SFO");
        
        assertEquals(request.branchCode(), "SFO");
    }

    @Test
    public void testAvailabilityUpdateRequestRecord() {
        InventoryInternalController.AvailabilityUpdateRequest request = 
                new InventoryInternalController.AvailabilityUpdateRequest(false);
        
        assertFalse(request.available());
    }
}
