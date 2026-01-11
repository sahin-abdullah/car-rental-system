package com.rental.car.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Detailed price calculation with line items")
public record PriceCalculationResponse(
    @Schema(description = "Number of rental days", example = "5")
    long rentalDays,
    
    @Schema(description = "Base daily rate used", example = "50.00")
    BigDecimal dailyRate,
    
    @Schema(description = "Time charge breakdown")
    TimeCharge timeCharge,
    
    @Schema(description = "Additional charges and fees")
    List<LineItem> additionalCharges,
    
    @Schema(description = "Subtotal before tax", example = "250.00")
    BigDecimal subtotal,
    
    @Schema(description = "Tax breakdown")
    List<LineItem> taxes,
    
    @Schema(description = "Total tax amount", example = "25.00")
    BigDecimal totalTax,
    
    @Schema(description = "Final total price", example = "275.00")
    BigDecimal totalPrice,
    
    @Schema(description = "Whether the car is available for these dates", example = "true")
    boolean available,
    
    @Schema(description = "Currency code", example = "USD")
    String currency
) {
    @Schema(description = "Time-based charge breakdown")
    public record TimeCharge(
        @Schema(description = "Number of full weeks", example = "0")
        int weeks,
        @Schema(description = "Remaining days", example = "5")
        int days,
        @Schema(description = "Weekly rate if applicable", example = "280.00")
        BigDecimal weeklyRate,
        @Schema(description = "Daily rate", example = "50.00")
        BigDecimal dailyRate,
        @Schema(description = "Total time charge", example = "250.00")
        BigDecimal amount
    ) {}
    
    @Schema(description = "Individual line item charge")
    public record LineItem(
        @Schema(description = "Item description", example = "One-way fee")
        String description,
        @Schema(description = "Item amount", example = "50.00")
        BigDecimal amount,
        @Schema(description = "Item type", example = "FEE")
        String type
    ) {}
}
