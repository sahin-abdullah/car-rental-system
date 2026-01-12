package com.rental.car.reservation;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ReservationStatusTest {

    @Test
    public void testEnumValues() {
        ReservationStatus[] statuses = ReservationStatus.values();
        
        assertEquals(statuses.length, 5);
        assertTrue(contains(statuses, ReservationStatus.PENDING));
        assertTrue(contains(statuses, ReservationStatus.CONFIRMED));
        assertTrue(contains(statuses, ReservationStatus.ACTIVE));
        assertTrue(contains(statuses, ReservationStatus.COMPLETED));
        assertTrue(contains(statuses, ReservationStatus.CANCELLED));
    }

    @Test
    public void testValueOf() {
        assertEquals(ReservationStatus.valueOf("PENDING"), ReservationStatus.PENDING);
        assertEquals(ReservationStatus.valueOf("CONFIRMED"), ReservationStatus.CONFIRMED);
        assertEquals(ReservationStatus.valueOf("ACTIVE"), ReservationStatus.ACTIVE);
        assertEquals(ReservationStatus.valueOf("COMPLETED"), ReservationStatus.COMPLETED);
        assertEquals(ReservationStatus.valueOf("CANCELLED"), ReservationStatus.CANCELLED);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValueOfInvalid() {
        ReservationStatus.valueOf("INVALID");
    }

    @Test
    public void testEnumEquality() {
        ReservationStatus status1 = ReservationStatus.PENDING;
        ReservationStatus status2 = ReservationStatus.PENDING;
        
        assertEquals(status1, status2);
        assertSame(status1, status2);
    }

    @Test
    public void testEnumName() {
        assertEquals(ReservationStatus.PENDING.name(), "PENDING");
        assertEquals(ReservationStatus.CONFIRMED.name(), "CONFIRMED");
        assertEquals(ReservationStatus.ACTIVE.name(), "ACTIVE");
    }

    @Test
    public void testEnumOrdinal() {
        assertEquals(ReservationStatus.PENDING.ordinal(), 0);
        assertEquals(ReservationStatus.CONFIRMED.ordinal(), 1);
        assertEquals(ReservationStatus.ACTIVE.ordinal(), 2);
        assertEquals(ReservationStatus.COMPLETED.ordinal(), 3);
        assertEquals(ReservationStatus.CANCELLED.ordinal(), 4);
    }

    private boolean contains(ReservationStatus[] statuses, ReservationStatus status) {
        for (ReservationStatus s : statuses) {
            if (s == status) {
                return true;
            }
        }
        return false;
    }
}
