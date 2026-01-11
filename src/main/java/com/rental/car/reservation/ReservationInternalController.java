package com.rental.car.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/v1/reservations")
@Tag(name = "Internal Reservation Management", description = "Internal APIs for managing reservations (admin/system use)")
class ReservationInternalController {

    private final ReservationService service;

    ReservationInternalController(ReservationService service) {
        this.service = service;
    }

    @Operation(summary = "Get all reservations", description = "Retrieve all reservations with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations")
    @GetMapping
    public Page<ReservationDTO> getAllReservations(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getAllReservations(pageable).map(ReservationDTO::from);
    }

    @Operation(summary = "Get reservations by status", description = "Filter reservations by status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations")
    @GetMapping("/status/{status}")
    public Page<ReservationDTO> getReservationsByStatus(
            @Parameter(description = "Reservation status", required = true) @PathVariable ReservationStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getReservationsByStatus(status, pageable).map(ReservationDTO::from);
    }

    @Operation(summary = "Confirm reservation", description = "Confirm a pending reservation (e.g., after payment)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid state transition"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id
    ) {
        Reservation reservation = service.confirmReservation(id);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Start reservation", description = "Mark reservation as active (car picked up)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation started successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid state transition"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/{id}/start")
    public ResponseEntity<ReservationDTO> startReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id
    ) {
        Reservation reservation = service.startReservation(id);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Complete reservation", description = "Mark reservation as completed (car returned)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid state transition"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/{id}/complete")
    public ResponseEntity<ReservationDTO> completeReservation(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id
    ) {
        Reservation reservation = service.completeReservation(id);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @Operation(summary = "Get reservation details", description = "Retrieve detailed reservation information")
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
}
