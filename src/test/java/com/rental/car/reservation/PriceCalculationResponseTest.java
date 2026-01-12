package com.rental.car.reservation;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.math.BigDecimal;

public class PriceCalculationResponseTest {

    @Test
    public void testPriceCalculationResponseCreation() {
        PriceCalculationResponse.TimeCharge timeCharge = new PriceCalculationResponse.TimeCharge(
            0, 5, new BigDecimal("280.00"), new BigDecimal("50.00"), new BigDecimal("250.00")
        );
        
        PriceCalculationResponse.LineItem lineItem = new PriceCalculationResponse.LineItem(
            "One-way fee", new BigDecimal("50.00"), "FEE"
        );
        
        PriceCalculationResponse response = new PriceCalculationResponse(
            5L, new BigDecimal("50.00"), timeCharge,
            java.util.Arrays.asList(lineItem),
            new BigDecimal("300.00"),
            java.util.Arrays.asList(),
            new BigDecimal("30.00"),
            new BigDecimal("330.00"),
            true, "USD"
        );

        assertEquals(response.rentalDays(), 5L);
        assertEquals(response.dailyRate(), new BigDecimal("50.00"));
        assertEquals(response.subtotal(), new BigDecimal("300.00"));
        assertEquals(response.totalPrice(), new BigDecimal("330.00"));
        assertTrue(response.available());
        assertEquals(response.currency(), "USD");
    }

    @Test
    public void testTimeChargeRecord() {
        PriceCalculationResponse.TimeCharge timeCharge = new PriceCalculationResponse.TimeCharge(
            1, 3, new BigDecimal("280.00"), new BigDecimal("50.00"), new BigDecimal("430.00")
        );

        assertEquals(timeCharge.weeks(), 1);
        assertEquals(timeCharge.days(), 3);
        assertEquals(timeCharge.weeklyRate(), new BigDecimal("280.00"));
        assertEquals(timeCharge.dailyRate(), new BigDecimal("50.00"));
        assertEquals(timeCharge.amount(), new BigDecimal("430.00"));
    }

    @Test
    public void testLineItemRecord() {
        PriceCalculationResponse.LineItem lineItem = new PriceCalculationResponse.LineItem(
            "Weekend surcharge", new BigDecimal("25.00"), "SURCHARGE"
        );

        assertEquals(lineItem.description(), "Weekend surcharge");
        assertEquals(lineItem.amount(), new BigDecimal("25.00"));
        assertEquals(lineItem.type(), "SURCHARGE");
    }
}
