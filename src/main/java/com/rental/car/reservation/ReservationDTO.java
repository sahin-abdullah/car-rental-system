package com.rental.car.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Reservation details")
public record ReservationDTO(
    @Schema(description = "Reservation ID", example = "1")
    Long id,
    
    @Schema(description = "Car ID", example = "10")
    Long carId,
    
    @Schema(description = "Customer email", example = "customer@example.com")
    String customerEmail,
    
    @Schema(description = "Customer name", example = "John Doe")
    String customerName,
    
    @Schema(description = "Customer phone", example = "+1-555-0100")
    String customerPhone,
    
    @Schema(description = "Pickup branch code", example = "LAX")
    String pickupBranchCode,
    
    @Schema(description = "Return branch code", example = "LAX")
    String returnBranchCode,
    
    @Schema(description = "Pickup date", example = "2026-01-15")
    LocalDate pickupDate,
    
    @Schema(description = "Return date", example = "2026-01-20")
    LocalDate returnDate,
    
    @Schema(description = "Reservation status", example = "CONFIRMED")
    ReservationStatus status,
    
    @Schema(description = "Total price", example = "250.00")
    BigDecimal totalPrice,
    
    @Schema(description = "Daily rate", example = "50.00")
    BigDecimal dailyRate,
    
    @Schema(description = "Additional notes")
    String notes,
    
    @Schema(description = "Number of rental days", example = "5")
    Long rentalDays,
    
    @Schema(description = "Creation timestamp", example = "2026-01-09T10:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Last update timestamp", example = "2026-01-09T10:30:00")
    LocalDateTime updatedAt
) {
    static ReservationDTO from(Reservation reservation) {
        return new ReservationDTO(
            reservation.getId(),
            reservation.getCarId(),
            reservation.getCustomerEmail(),
            reservation.getCustomerName(),
            reservation.getCustomerPhone(),
            reservation.getPickupBranchCode(),
            reservation.getReturnBranchCode(),
            reservation.getPickupDate(),
            reservation.getReturnDate(),
            reservation.getStatus(),
            reservation.getTotalPrice(),
            reservation.getDailyRate(),
            reservation.getNotes(),
            reservation.getRentalDays(),
            reservation.getCreatedAt(),
            reservation.getUpdatedAt()
        );
    }
}
