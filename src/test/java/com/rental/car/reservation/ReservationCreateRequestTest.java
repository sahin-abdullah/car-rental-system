package com.rental.car.reservation;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.time.LocalDate;

public class ReservationCreateRequestTest {

    @Test
    public void testValidReservationCreateRequest() {
        ReservationCreateRequest request = new ReservationCreateRequest(
            10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "SFO", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
            "Need GPS"
        );

        assertEquals(request.carId(), Long.valueOf(10L));
        assertEquals(request.customerEmail(), "customer@example.com");
        assertEquals(request.customerName(), "John Doe");
        assertEquals(request.pickupBranchCode(), "LAX");
        assertEquals(request.returnBranchCode(), "SFO");
        assertEquals(request.notes(), "Need GPS");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidDateRange() {
        new ReservationCreateRequest(
            10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "LAX", LocalDate.now().plusDays(10), LocalDate.now().plusDays(5),
            null
        );
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSamePickupAndReturnDate() {
        LocalDate date = LocalDate.now().plusDays(5);
        new ReservationCreateRequest(
            10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "LAX", date, date,
            null
        );
    }

    @Test
    public void testRequestWithoutNotes() {
        ReservationCreateRequest request = new ReservationCreateRequest(
            10L, "customer@example.com", "John Doe", null,
            "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
            null
        );

        assertNull(request.notes());
        assertNull(request.customerPhone());
    }
}
