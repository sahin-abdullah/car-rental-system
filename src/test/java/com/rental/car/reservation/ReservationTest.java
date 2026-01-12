package com.rental.car.reservation;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationTest {

    private Reservation reservation;

    @BeforeMethod
    public void setUp() {
        reservation = new Reservation();
    }

    @Test
    public void testReservationCreation() {
        Reservation r = new Reservation(
            1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "SFO", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 20),
            ReservationStatus.PENDING, new BigDecimal("250.00"), new BigDecimal("50.00"),
            "Need GPS", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 0
        );

        assertEquals(r.getId(), Long.valueOf(1L));
        assertEquals(r.getCarId(), Long.valueOf(10L));
        assertEquals(r.getCustomerEmail(), "customer@example.com");
        assertEquals(r.getCustomerName(), "John Doe");
        assertEquals(r.getPickupBranchCode(), "LAX");
        assertEquals(r.getReturnBranchCode(), "SFO");
        assertEquals(r.getStatus(), ReservationStatus.PENDING);
    }

    @Test
    public void testSettersAndGetters() {
        reservation.setId(5L);
        reservation.setCarId(20L);
        reservation.setCustomerEmail("test@test.com");
        reservation.setCustomerName("Jane Smith");
        reservation.setCustomerPhone("+1-555-0200");
        reservation.setPickupBranchCode("NYC");
        reservation.setReturnBranchCode("NYC");
        reservation.setPickupDate(LocalDate.of(2026, 2, 1));
        reservation.setReturnDate(LocalDate.of(2026, 2, 5));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setTotalPrice(new BigDecimal("300.00"));
        reservation.setDailyRate(new BigDecimal("60.00"));
        reservation.setNotes("Premium car");

        assertEquals(reservation.getId(), Long.valueOf(5L));
        assertEquals(reservation.getCarId(), Long.valueOf(20L));
        assertEquals(reservation.getCustomerEmail(), "test@test.com");
        assertEquals(reservation.getCustomerName(), "Jane Smith");
        assertEquals(reservation.getPickupBranchCode(), "NYC");
        assertEquals(reservation.getReturnBranchCode(), "NYC");
        assertEquals(reservation.getStatus(), ReservationStatus.CONFIRMED);
        assertEquals(reservation.getTotalPrice(), new BigDecimal("300.00"));
    }

    @Test
    public void testGetRentalDays() {
        reservation.setPickupDate(LocalDate.of(2026, 1, 15));
        reservation.setReturnDate(LocalDate.of(2026, 1, 20));

        assertEquals(reservation.getRentalDays(), 5L);
    }

    @Test
    public void testIsExpiredWhenPendingAndExpired() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        assertTrue(reservation.isExpired());
    }

    @Test
    public void testIsExpiredWhenPendingButNotExpired() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        assertFalse(reservation.isExpired());
    }

    @Test
    public void testIsExpiredWhenConfirmed() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        assertFalse(reservation.isExpired());
    }

    @Test
    public void testIsExpiredWhenNoExpiresAt() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(null);

        assertFalse(reservation.isExpired());
    }

    @Test
    public void testOnCreate() {
        reservation.onCreate();

        assertNotNull(reservation.getCreatedAt());
        assertNotNull(reservation.getUpdatedAt());
        assertEquals(reservation.getStatus(), ReservationStatus.PENDING);
    }

    @Test
    public void testOnUpdate() {
        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        reservation.setUpdatedAt(originalTime);
        
        reservation.onUpdate();
        
        assertTrue(reservation.getUpdatedAt().isAfter(originalTime));
    }
}
