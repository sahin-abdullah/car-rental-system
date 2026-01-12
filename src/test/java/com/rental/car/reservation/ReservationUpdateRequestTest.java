package com.rental.car.reservation;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.time.LocalDate;

public class ReservationUpdateRequestTest {

    @Test
    public void testValidReservationUpdateRequest() {
        ReservationUpdateRequest request = new ReservationUpdateRequest(
            LocalDate.now().plusDays(6), LocalDate.now().plusDays(11), "Updated notes"
        );

        assertNotNull(request.pickupDate());
        assertNotNull(request.returnDate());
        assertEquals(request.notes(), "Updated notes");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidDateRange() {
        new ReservationUpdateRequest(
            LocalDate.now().plusDays(10), LocalDate.now().plusDays(5), null
        );
    }

    @Test
    public void testPartialUpdate() {
        ReservationUpdateRequest request = new ReservationUpdateRequest(
            LocalDate.now().plusDays(6), null, "Only update pickup date"
        );

        assertNotNull(request.pickupDate());
        assertNull(request.returnDate());
    }

    @Test
    public void testUpdateOnlyNotes() {
        ReservationUpdateRequest request = new ReservationUpdateRequest(
            null, null, "New notes"
        );

        assertNull(request.pickupDate());
        assertNull(request.returnDate());
        assertEquals(request.notes(), "New notes");
    }
}
