package com.rental.car.reservation;

import com.rental.car.inventory.CarType;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class PricingServiceTest {

    @Mock
    private RatePlanRepository ratePlanRepo;

    @Mock
    private PricingRuleRepository pricingRuleRepo;

    private PricingService pricingService;

    private RatePlan ratePlan;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pricingService = new PricingService(ratePlanRepo, pricingRuleRepo);

        ratePlan = new RatePlan(
                1L, "LAX", CarType.SEDAN, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                new BigDecimal("50.00"), new BigDecimal("280.00"), new BigDecimal("1.2"),
                "USD", "Standard rate", true
        );
    }

    @Test
    public void testCalculatePrice() {
        when(ratePlanRepo.findApplicableRatePlan(anyString(), any(CarType.class), any(LocalDate.class)))
                .thenReturn(Optional.of(ratePlan));
        when(pricingRuleRepo.findByRuleCodeAndActiveTrue("SALES_TAX"))
                .thenReturn(Optional.empty());

        PriceCalculationResponse result = pricingService.calculatePrice(
                CarType.SEDAN, "LAX", "LAX",
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), true
        );

        assertNotNull(result);
        assertEquals(result.rentalDays(), 5L);
        assertEquals(result.dailyRate(), new BigDecimal("50.00"));
        assertTrue(result.available());
    }

    @Test
    public void testCalculatePriceWithOneWayFee() {
        when(ratePlanRepo.findApplicableRatePlan(anyString(), any(CarType.class), any(LocalDate.class)))
                .thenReturn(Optional.of(ratePlan));
        when(pricingRuleRepo.findByRuleCodeAndActiveTrue("ONE_WAY_FEE"))
                .thenReturn(Optional.empty());
        when(pricingRuleRepo.findByRuleCodeAndActiveTrue("SALES_TAX"))
                .thenReturn(Optional.empty());

        PriceCalculationResponse result = pricingService.calculatePrice(
                CarType.SEDAN, "LAX", "SFO",
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), true
        );

        assertNotNull(result);
        assertFalse(result.additionalCharges().isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCalculatePriceInvalidDates() {
        pricingService.calculatePrice(
                CarType.SEDAN, "LAX", "LAX",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(5), true
        );
    }

    @Test
    public void testCalculatePriceWithDefaultRatePlan() {
        when(ratePlanRepo.findApplicableRatePlan(anyString(), any(CarType.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(pricingRuleRepo.findByRuleCodeAndActiveTrue("SALES_TAX"))
                .thenReturn(Optional.empty());

        PriceCalculationResponse result = pricingService.calculatePrice(
                CarType.SUV, "NYC", "NYC",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(4), true
        );

        assertNotNull(result);
        assertEquals(result.rentalDays(), 3L);
    }
}
