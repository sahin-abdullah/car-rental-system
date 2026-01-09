package com.rental.car.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Car inventory and branch management API")
class InventorySearchController {

    private final InventoryService service;

    InventorySearchController(InventoryService service) {
        this.service = service;
    }

    @Operation(summary = "Search available cars", description = "Search for available cars with optional filters. Can search by address (finds nearby branches) or by specific branch.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cars"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/cars")
    public Page<CarDTO> searchFleet(
            @Parameter(description = "Address to search near (e.g., 'Los Angeles, CA')") @RequestParam(required = false) String address,
            @Parameter(description = "Branch code (e.g., 'LAX')") @RequestParam(required = false) String branch, 
            @Parameter(description = "Car type filter") @RequestParam(required = false) CarType type,  
            @Parameter(description = "Car make filter (e.g., 'Toyota')") @RequestParam(required = false) String make,   
            @Parameter(description = "Car year filter") @RequestParam(required = false) Integer year,  
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return service.searchWithFilters(address, branch, type, make, year, pageable);
    }

    @Operation(summary = "Get car by ID", description = "Retrieve detailed information about a specific car")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car found"),
        @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCarById(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id) {
        return service.getCarById(id)
                .map(CarDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List all branches", description = "Get a paginated list of all rental branches")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved branches")
    @GetMapping("/branches")
    public Page<BranchDTO> getAllBranches(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getAllBranches(pageable).map(BranchDTO::from);
    }

    @Operation(summary = "Get branch by code", description = "Retrieve detailed information about a specific branch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Branch found"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @GetMapping("/branches/{code}")
    public ResponseEntity<BranchDTO> getBranchByCode(
            @Parameter(description = "Branch code (e.g., 'LAX')", required = true) @PathVariable String code) {
        return service.getBranchByCode(code)
                .map(BranchDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Find nearest branches", description = "Find the 5 nearest rental branches to a given location")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved nearest branches")
    @GetMapping("/branches/nearest")
    public List<BranchWithDistance> getNearestBranches(
            @Parameter(description = "Latitude", required = true, example = "34.0522") @RequestParam double lat, 
            @Parameter(description = "Longitude", required = true, example = "-118.2437") @RequestParam double lon
    ) {
        return service.findNearestBranches(lat, lon, 5);
    }

    @Operation(summary = "Get available filters for branch", 
               description = "Get available car types, makes, or years at a specific branch. Use hierarchical filtering: types → makes (with type) → years (with type and make)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filters"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameter")
    })
    @GetMapping("/branches/{code}/filters")
    public ResponseEntity<?> getBranchFilters(
            @Parameter(description = "Branch code", required = true, example = "LAX") @PathVariable String code,
            @Parameter(description = "Filter type: 'types', 'makes', or 'years'", required = true, example = "types") @RequestParam String filter,
            @Parameter(description = "Car type (for makes/years filters)") @RequestParam(required = false) CarType type,
            @Parameter(description = "Car make (for years filter)") @RequestParam(required = false) String make
    ) {
        return switch (filter.toLowerCase()) {
            case "types" -> ResponseEntity.ok(service.getAvailableTypesAtBranch(code));
            case "makes" -> ResponseEntity.ok(service.getAvailableMakesAtBranch(code, type));
            case "years" -> ResponseEntity.ok(service.getAvailableYearsAtBranch(code, type, make));
            default -> ResponseEntity.badRequest().body("Invalid filter. Use: types, makes, or years");
        };
    }
}
