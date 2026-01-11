package com.rental.car.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservations", description = "Customer reservation management API")
class ReservationController {

    private final ReservationService service;

    ReservationController(ReservationService service) {
        this.service = service;
    }

    @Operation(summary = "Calculate price", description = "Calculate rental price for a car and date range without creating a reservation")
    @ApiResponse(responseCode = "200", description = "Price calculated successfully")
    @GetMapping("/price")
    public PriceCalculationResponse calculatePrice(
            @Parameter(description = "Car ID", required = true, example = "10") @RequestParam Long carId,
            @Parameter(description = "Pickup date", required = true, example = "2026-01-15") @RequestParam LocalDate pickupDate,
            @Parameter(description = "Return date", required = true, example = "2026-01-20") @RequestParam LocalDate returnDate
    ) {
        return service.calculatePrice(carId, pickupDate, returnDate);
    }

    @Operation(summary = "Create reservation", description = "Create a new car reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Car or branch not found"),
        @ApiResponse(responseCode = "409", description = "Reservation conflict - car already booked for these dates")
    })
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
        Reservation reservation = service.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Get reservation by ID", description = "Retrieve reservation details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation found"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id
    ) {
        Reservation reservation = service.getReservationById(id);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Update reservation", description = "Update reservation dates and notes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid state or dates"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "409", description = "New dates conflict with existing reservations")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        Reservation reservation = service.updateReservation(id, request);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Cancel reservation", description = "Cancel an existing reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel completed reservation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationDTO> cancelReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id
    ) {
        Reservation reservation = service.cancelReservation(id);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Get customer reservations", description = "Get all reservations for a customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations")
    @GetMapping("/customer/{email}")
    public Page<ReservationDTO> getCustomerReservations(
            @Parameter(description = "Customer email", required = true) @PathVariable String email,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getCustomerReservations(email, pageable).map(ReservationDTO::from);
    }

    @Operation(summary = "Get upcoming reservations", description = "Get customer's upcoming reservations")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved upcoming reservations")
    @GetMapping("/customer/{email}/upcoming")
    public List<ReservationDTO> getUpcomingReservations(
            @Parameter(description = "Customer email", required = true) @PathVariable String email
    ) {
        return service.getUpcomingReservations(email).stream()
                .map(ReservationDTO::from)
                .toList();
    }
}
