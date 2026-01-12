package com.rental.car.reservation;

import com.rental.car.inventory.CarType;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RatePlanTest {

    private RatePlan ratePlan;

    @BeforeMethod
    public void setUp() {
        ratePlan = new RatePlan();
    }

    @Test
    public void testRatePlanCreation() {
        RatePlan rp = new RatePlan(
            1L, "LAX", CarType.SEDAN, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
            new BigDecimal("50.00"), new BigDecimal("280.00"), new BigDecimal("1.2"),
            "USD", "Standard rate for 2026", true
        );

        assertEquals(rp.getId(), Long.valueOf(1L));
        assertEquals(rp.getBranchCode(), "LAX");
        assertEquals(rp.getCarType(), CarType.SEDAN);
        assertEquals(rp.getDailyRate(), new BigDecimal("50.00"));
        assertEquals(rp.getWeeklyRate(), new BigDecimal("280.00"));
        assertTrue(rp.isActive());
    }

    @Test
    public void testSettersAndGetters() {
        ratePlan.setId(5L);
        ratePlan.setBranchCode("SFO");
        ratePlan.setCarType(CarType.SUV);
        ratePlan.setEffectiveFrom(LocalDate.of(2026, 2, 1));
        ratePlan.setEffectiveTo(LocalDate.of(2026, 2, 28));
        ratePlan.setDailyRate(new BigDecimal("75.00"));
        ratePlan.setWeeklyRate(new BigDecimal("420.00"));
        ratePlan.setWeekendMultiplier(new BigDecimal("1.3"));
        ratePlan.setCurrency("USD");
        ratePlan.setDescription("February special");
        ratePlan.setActive(true);

        assertEquals(ratePlan.getBranchCode(), "SFO");
        assertEquals(ratePlan.getCarType(), CarType.SUV);
        assertEquals(ratePlan.getDailyRate(), new BigDecimal("75.00"));
        assertEquals(ratePlan.getWeeklyRate(), new BigDecimal("420.00"));
        assertEquals(ratePlan.getWeekendMultiplier(), new BigDecimal("1.3"));
    }

    @Test
    public void testIsValidForDateInRange() {
        ratePlan.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        ratePlan.setEffectiveTo(LocalDate.of(2026, 12, 31));
        ratePlan.setActive(true);

        assertTrue(ratePlan.isValidForDate(LocalDate.of(2026, 6, 15)));
        assertTrue(ratePlan.isValidForDate(LocalDate.of(2026, 1, 1)));
        assertTrue(ratePlan.isValidForDate(LocalDate.of(2026, 12, 31)));
    }

    @Test
    public void testIsValidForDateOutOfRange() {
        ratePlan.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        ratePlan.setEffectiveTo(LocalDate.of(2026, 12, 31));
        ratePlan.setActive(true);

        assertFalse(ratePlan.isValidForDate(LocalDate.of(2025, 12, 31)));
        assertFalse(ratePlan.isValidForDate(LocalDate.of(2027, 1, 1)));
    }

    @Test
    public void testIsValidForDateWhenInactive() {
        ratePlan.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        ratePlan.setEffectiveTo(LocalDate.of(2026, 12, 31));
        ratePlan.setActive(false);

        assertFalse(ratePlan.isValidForDate(LocalDate.of(2026, 6, 15)));
    }

    @Test
    public void testDefaultCurrency() {
        RatePlan rp = new RatePlan();
        assertEquals(rp.getCurrency(), "USD");
    }

    @Test
    public void testDefaultActive() {
        RatePlan rp = new RatePlan();
        assertTrue(rp.isActive());
    }
}
