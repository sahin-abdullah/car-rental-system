package com.rental.car.reservation;

import com.rental.car.inventory.CarType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PricingService {

    private final RatePlanRepository ratePlanRepo;
    private final PricingRuleRepository pricingRuleRepo;
    private final com.rental.car.inventory.InventoryService inventoryService;

    // Fallback rates if no rate plan found
    private static final BigDecimal DEFAULT_DAILY_RATE = new BigDecimal("50.00");
    private static final BigDecimal DEFAULT_WEEKLY_RATE = new BigDecimal("280.00"); // ~20% discount
    private static final String DEFAULT_CURRENCY = "USD";

    public PricingService(
            RatePlanRepository ratePlanRepo, 
            PricingRuleRepository pricingRuleRepo,
            com.rental.car.inventory.InventoryService inventoryService
    ) {
        this.ratePlanRepo = ratePlanRepo;
        this.pricingRuleRepo = pricingRuleRepo;
        this.inventoryService = inventoryService;
    }

    /**
     * Calculate comprehensive price quote for a reservation
     */
    @Transactional(readOnly = true)
    public PriceCalculationResponse calculatePrice(
            CarType carType,
            String pickupBranchCode,
            String returnBranchCode,
            LocalDate pickupDate,
            LocalDate returnDate,
            boolean available
    ) {
        long totalDays = ChronoUnit.DAYS.between(pickupDate, returnDate);
        if (totalDays < 1) {
            throw new IllegalArgumentException("Rental period must be at least 1 day");
        }

        // 1. Get applicable rate plan
        RatePlan ratePlan = ratePlanRepo.findApplicableRatePlan(
            pickupBranchCode, carType, pickupDate
        ).orElse(createDefaultRatePlan(pickupBranchCode, carType));

        // 2. Calculate time charge (optimize for weekly rates)
        PriceCalculationResponse.TimeCharge timeCharge = calculateTimeCharge(
            totalDays, ratePlan.getDailyRate(), ratePlan.getWeeklyRate()
        );

        // 3. Calculate additional charges
        List<PriceCalculationResponse.LineItem> additionalCharges = new ArrayList<>();
        BigDecimal additionalTotal = BigDecimal.ZERO;

        // Airport fee (if pickup is at an airport location)
        if (inventoryService.isAirportBranch(pickupBranchCode)) {
            BigDecimal airportFee = getAirportFee();
            additionalCharges.add(new PriceCalculationResponse.LineItem(
                "Airport facility fee", airportFee, "FEE"
            ));
            additionalTotal = additionalTotal.add(airportFee);
        }

        // One-way fee
        if (!pickupBranchCode.equals(returnBranchCode)) {
            BigDecimal oneWayFee = getOneWayFee();
            additionalCharges.add(new PriceCalculationResponse.LineItem(
                "One-way fee (different return location)", oneWayFee, "FEE"
            ));
            additionalTotal = additionalTotal.add(oneWayFee);
        }

        // Weekend surcharge (if applicable)
        BigDecimal weekendSurcharge = calculateWeekendSurcharge(
            pickupDate, returnDate, ratePlan.getDailyRate(), ratePlan.getWeekendMultiplier()
        );
        if (weekendSurcharge.compareTo(BigDecimal.ZERO) > 0) {
            additionalCharges.add(new PriceCalculationResponse.LineItem(
                "Weekend surcharge", weekendSurcharge, "SURCHARGE"
            ));
            additionalTotal = additionalTotal.add(weekendSurcharge);
        }

        // 4. Calculate subtotal before discounts
        BigDecimal subtotal = timeCharge.amount().add(additionalTotal);

        // 5. Apply length-based discounts
        List<PriceCalculationResponse.LineItem> discounts = new ArrayList<>();
        BigDecimal discountTotal = BigDecimal.ZERO;

        // Monthly discount (30+ days, 18%)
        if (totalDays >= 30) {
            BigDecimal monthlyDiscount = calculateLengthDiscount(subtotal, "MONTHLY_DISCOUNT");
            if (monthlyDiscount.compareTo(BigDecimal.ZERO) > 0) {
                discounts.add(new PriceCalculationResponse.LineItem(
                    "Monthly rental discount (30+ days)", monthlyDiscount.negate(), "DISCOUNT"
                ));
                discountTotal = discountTotal.add(monthlyDiscount);
            }
        }
        // Weekly discount (7+ days, 12%) - only if not already getting monthly
        else if (totalDays >= 7) {
            BigDecimal weeklyDiscount = calculateLengthDiscount(subtotal, "WEEKLY_DISCOUNT");
            if (weeklyDiscount.compareTo(BigDecimal.ZERO) > 0) {
                discounts.add(new PriceCalculationResponse.LineItem(
                    "Weekly rental discount (7+ days)", weeklyDiscount.negate(), "DISCOUNT"
                ));
                discountTotal = discountTotal.add(weeklyDiscount);
            }
        }

        // Add discounts to additional charges list
        additionalCharges.addAll(discounts);

        // Recalculate subtotal with discounts
        subtotal = subtotal.subtract(discountTotal);

        // 5. Calculate taxes
        List<PriceCalculationResponse.LineItem> taxes = new ArrayList<>();
        BigDecimal salesTax = calculateSalesTax(subtotal);
        taxes.add(new PriceCalculationResponse.LineItem(
            "Sales Tax", salesTax, "TAX"
        ));
        BigDecimal totalTax = salesTax;

        // 6. Calculate total
        BigDecimal totalPrice = subtotal.add(totalTax).setScale(2, RoundingMode.HALF_UP);

        return new PriceCalculationResponse(
            totalDays,
            ratePlan.getDailyRate(),
            timeCharge,
            additionalCharges,
            subtotal,
            taxes,
            totalTax,
            totalPrice,
            available,
            ratePlan.getCurrency()
        );
    }

    /**
     * Calculate time charge optimizing for weekly rates
     */
    private PriceCalculationResponse.TimeCharge calculateTimeCharge(
            long totalDays, 
            BigDecimal dailyRate, 
            BigDecimal weeklyRate
    ) {
        int weeks = (int) (totalDays / 7);
        int remainingDays = (int) (totalDays % 7);

        // Check if using weekly rate is beneficial
        BigDecimal weekCharge = weeklyRate.multiply(BigDecimal.valueOf(weeks));
        BigDecimal dayCharge = dailyRate.multiply(BigDecimal.valueOf(remainingDays));
        BigDecimal totalAmount = weekCharge.add(dayCharge).setScale(2, RoundingMode.HALF_UP);

        return new PriceCalculationResponse.TimeCharge(
            weeks,
            remainingDays,
            weeklyRate,
            dailyRate,
            totalAmount
        );
    }

    /**
     * Calculate weekend surcharge based on weekend days in rental period
     */
    private BigDecimal calculateWeekendSurcharge(
        LocalDate pickupDate, 
        LocalDate returnDate,
        BigDecimal dailyRate,
        BigDecimal weekendMultiplier
    ) {
        if (weekendMultiplier == null || weekendMultiplier.compareTo(BigDecimal.ONE) <= 0) {
            return BigDecimal.ZERO;
        }

        // Count weekend days
        long weekendDays = 0;
        LocalDate current = pickupDate;
        while (current.isBefore(returnDate)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendDays++;
            }
            current = current.plusDays(1);
        }

        if (weekendDays == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate surcharge: dailyRate * (multiplier - 1.0) * weekendDays
        BigDecimal uplift = weekendMultiplier.subtract(BigDecimal.ONE);
        return dailyRate.multiply(uplift)
                        .multiply(BigDecimal.valueOf(weekendDays))
                        .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get one-way fee from pricing rules or default
     */
    private BigDecimal getOneWayFee() {
        return pricingRuleRepo.findByRuleCodeAndActiveTrue("ONE_WAY_FEE")
            .map(PricingRule::getFixedAmount)
            .orElse(new BigDecimal("50.00"));
    }

    /**
     * Get airport fee from pricing rules or default
     */
    private BigDecimal getAirportFee() {
        return pricingRuleRepo.findByRuleCodeAndActiveTrue("AIRPORT_FEE_LOGAN")
            .map(PricingRule::getFixedAmount)
            .orElse(new BigDecimal("25.00"));
    }

    /**
     * Calculate length-based discount (weekly, monthly, extended)
     */
    private BigDecimal calculateLengthDiscount(BigDecimal amount, String ruleCode) {
        BigDecimal discountPercentage = pricingRuleRepo.findByRuleCodeAndActiveTrue(ruleCode)
            .map(PricingRule::getPercentageValue)
            .orElse(BigDecimal.ZERO);

        if (discountPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate sales tax
     */
    private BigDecimal calculateSalesTax(BigDecimal amount) {
        BigDecimal taxRate = pricingRuleRepo.findByRuleCodeAndActiveTrue("MA_SALES_TAX")
            .map(PricingRule::getPercentageValue)
            .orElse(new BigDecimal("10.00"));

        return amount.multiply(taxRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Create default rate plan if none found
     */
    private RatePlan createDefaultRatePlan(String branchCode, CarType carType) {
        return new RatePlan(
            null,
            branchCode,
            carType,
            LocalDate.now().minusYears(1),
            LocalDate.now().plusYears(1),
            DEFAULT_DAILY_RATE,
            DEFAULT_WEEKLY_RATE,
            null,
            DEFAULT_CURRENCY,
            "Default rate plan",
            true
        );
    }
}
