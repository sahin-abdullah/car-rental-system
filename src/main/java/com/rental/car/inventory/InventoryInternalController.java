package com.rental.car.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/v1/inventory")
@Tag(name = "Internal Inventory Management", description = "Internal APIs for managing cars and branches (admin/system use)")
class InventoryInternalController {

    private final InventoryService service;

    InventoryInternalController(InventoryService service) {
        this.service = service;
    }

    @Operation(summary = "Check if branch exists", description = "Verify if a branch with the given code exists")
    @ApiResponse(responseCode = "200", description = "Returns true if branch exists, false otherwise")
    @GetMapping("/branches/{code}/exists")
    public boolean checkBranchExists(
            @Parameter(description = "Branch code", required = true) @PathVariable String code) {
        return service.isValidBranch(code);
    }

    @Operation(summary = "Get branch (internal)", description = "Internal endpoint to retrieve branch details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Branch found"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @GetMapping("/branches/{code}")
    public ResponseEntity<BranchDTO> getBranchByCode(
            @Parameter(description = "Branch code", required = true) @PathVariable String code) {
        return service.getBranchByCode(code)
                .map(BranchDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get car (internal)", description = "Internal endpoint to retrieve car details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car found"),
        @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCarById(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id) {
        return service.getCarById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update car availability", description = "Mark a car as available or unavailable")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability updated"),
        @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PostMapping("/cars/{id}/availability")
    public ResponseEntity<Void> updateCarAvailability(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id,
            @RequestBody AvailabilityUpdateRequest request
    ) {
        service.updateCarAvailability(id, request.available());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Move car to branch", description = "Transfer a car to a different branch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car moved successfully"),
        @ApiResponse(responseCode = "404", description = "Car or branch not found")
    })
    @PutMapping("/cars/{id}/branch")
    public ResponseEntity<Void> moveCarToBranch(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id,
            @RequestBody BranchTransferRequest request
    ) {
        service.moveCarToBranch(id, request.branchCode());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create new car", description = "Add a new car to the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car created successfully"),
        @ApiResponse(responseCode = "409", description = "License plate already exists")
    })
    @PostMapping("/cars")
    public ResponseEntity<CarDTO> createCar(@RequestBody CarCreateRequest request) {
        Car car = service.createCar(
            request.type(),
            request.licensePlate(),
            request.make(),
            request.model(),
            request.year(),
            request.branchCode(),
            request.available() != null ? request.available() : true
        );
        return ResponseEntity.ok(CarDTO.from(car));
    }

    @Operation(summary = "Update car", description = "Update car details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car updated successfully"),
        @ApiResponse(responseCode = "404", description = "Car not found"),
        @ApiResponse(responseCode = "409", description = "License plate already in use")
    })
    @PutMapping("/cars/{id}")
    public ResponseEntity<CarDTO> updateCar(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id,
            @RequestBody CarUpdateRequest request
    ) {
        Car car = service.updateCar(
            id,
            request.type(),
            request.licensePlate(),
            request.make(),
            request.model(),
            request.year(),
            request.branchCode(),
            request.available()
        );
        return ResponseEntity.ok(CarDTO.from(car));
    }

    @Operation(summary = "Delete car", description = "Remove a car from the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id) {
        service.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create branch", description = "Create a new rental branch with geocoded address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Branch created successfully"),
        @ApiResponse(responseCode = "409", description = "Branch code already exists")
    })
    @PostMapping("/branches")
    public ResponseEntity<BranchDTO> createBranch(@RequestBody BranchCreateRequest request) {
        Branch branch = service.createBranchWithDetails(
            request.code(),
            request.name(),
            request.phoneNumber(),
            request.street(),
            request.city(),
            request.state(),
            request.country(),
            request.zipCode()
        );
        return ResponseEntity.ok(BranchDTO.from(branch));
    }

    @Operation(summary = "Update branch", description = "Update branch details. Address changes trigger re-geocoding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Branch updated successfully"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @PutMapping("/branches/{code}")
    public ResponseEntity<BranchDTO> updateBranch(
            @Parameter(description = "Branch code", required = true) @PathVariable String code,
            @RequestBody BranchUpdateRequest request
    ) {
        Branch branch = service.updateBranch(
            code,
            request.name(),
            request.phoneNumber(),
            request.street(),
            request.city(),
            request.state(),
            request.country(),
            request.zipCode()
        );
        return ResponseEntity.ok(BranchDTO.from(branch));
    }

    @Operation(summary = "Delete branch", description = "Remove a branch from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Branch deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @DeleteMapping("/branches/{code}")
    public ResponseEntity<Void> deleteBranch(
            @Parameter(description = "Branch code", required = true) @PathVariable String code) {
        service.deleteBranch(code);
        return ResponseEntity.noContent().build();
    }

    record CarCreateRequest(
        CarType type,
        String licensePlate,
        String make,
        String model,
        int year,
        String branchCode,
        Boolean available
    ) {}

    record CarUpdateRequest(
        CarType type,
        String licensePlate,
        String make,
        String model,
        int year,
        String branchCode,
        boolean available
    ) {}

    record BranchCreateRequest(
        String code,
        String name,
        String phoneNumber,
        String street,
        String city,
        String state,
        String country,
        String zipCode
    ) {}

    record BranchUpdateRequest(
        String name,
        String phoneNumber,
        String street,
        String city,
        String state,
        String country,
        String zipCode
    ) {}

    record AvailabilityUpdateRequest(boolean available) {}

    record BranchTransferRequest(String branchCode) {}
}
