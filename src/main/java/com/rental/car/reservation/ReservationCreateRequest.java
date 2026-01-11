package com.rental.car.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(description = "Request to create a new reservation")
public record ReservationCreateRequest(
    @Schema(description = "Car ID to reserve", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Car ID is required")
    Long carId,
    
    @Schema(description = "Customer email address", example = "customer@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    String customerEmail,
    
    @Schema(description = "Customer full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Customer name is required")
    String customerName,
    
    @Schema(description = "Customer phone number", example = "+1-555-0100")
    String customerPhone,
    
    @Schema(description = "Pickup branch code", example = "LAX", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Pickup branch is required")
    String pickupBranchCode,
    
    @Schema(description = "Return branch code", example = "LAX", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Return branch is required")
    String returnBranchCode,
    
    @Schema(description = "Pickup date (must be future)", example = "2026-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Pickup date is required")
    @Future(message = "Pickup date must be in the future")
    LocalDate pickupDate,
    
    @Schema(description = "Return date (must be after pickup)", example = "2026-01-20", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Return date is required")
    LocalDate returnDate,
    
    @Schema(description = "Additional notes", example = "Need child seat")
    String notes
) {

    public ReservationCreateRequest {
        if (returnDate != null && pickupDate != null && !returnDate.isAfter(pickupDate)) {
            throw new IllegalArgumentException("Return date must be after pickup date");
        }
    }
}
