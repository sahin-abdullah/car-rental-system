package com.rental.car.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(description = "Request to update reservation dates")
public record ReservationUpdateRequest(
    @Schema(description = "New pickup date", example = "2026-01-16")
    @Future(message = "Pickup date must be in the future")
    LocalDate pickupDate,
    
    @Schema(description = "New return date", example = "2026-01-22")
    LocalDate returnDate,
    
    @Schema(description = "Updated notes", example = "Changed to sedan")
    String notes
) {
    public ReservationUpdateRequest {
        if (returnDate != null && pickupDate != null && !returnDate.isAfter(pickupDate)) {
            throw new IllegalArgumentException("Return date must be after pickup date");
        }
    }
}
