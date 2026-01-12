package com.rental.car.reservation;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationDTOTest {

    private Reservation reservation;

    @BeforeMethod
    public void setUp() {
        reservation = new Reservation(
            1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "SFO", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 20),
            ReservationStatus.CONFIRMED, new BigDecimal("250.00"), new BigDecimal("50.00"),
            "Need GPS", LocalDateTime.of(2026, 1, 10, 10, 0), 
            LocalDateTime.of(2026, 1, 10, 10, 0), null, 0
        );
    }

    @Test
    public void testReservationDTOCreation() {
        ReservationDTO dto = new ReservationDTO(
            1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "SFO", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 20),
            ReservationStatus.CONFIRMED, new BigDecimal("250.00"), new BigDecimal("50.00"),
            "Need GPS", 5L, LocalDateTime.of(2026, 1, 10, 10, 0),
            LocalDateTime.of(2026, 1, 10, 10, 0)
        );

        assertEquals(dto.id(), Long.valueOf(1L));
        assertEquals(dto.carId(), Long.valueOf(10L));
        assertEquals(dto.customerEmail(), "customer@example.com");
        assertEquals(dto.customerName(), "John Doe");
        assertEquals(dto.pickupBranchCode(), "LAX");
        assertEquals(dto.returnBranchCode(), "SFO");
        assertEquals(dto.status(), ReservationStatus.CONFIRMED);
        assertEquals(dto.totalPrice(), new BigDecimal("250.00"));
        assertEquals(dto.rentalDays(), Long.valueOf(5L));
    }

    @Test
    public void testFromReservation() {
        ReservationDTO dto = ReservationDTO.from(reservation);

        assertNotNull(dto);
        assertEquals(dto.id(), reservation.getId());
        assertEquals(dto.carId(), reservation.getCarId());
        assertEquals(dto.customerEmail(), reservation.getCustomerEmail());
        assertEquals(dto.customerName(), reservation.getCustomerName());
        assertEquals(dto.pickupBranchCode(), reservation.getPickupBranchCode());
        assertEquals(dto.returnBranchCode(), reservation.getReturnBranchCode());
        assertEquals(dto.status(), reservation.getStatus());
        assertEquals(dto.totalPrice(), reservation.getTotalPrice());
        assertEquals(dto.rentalDays(), Long.valueOf(5L));
    }

    @Test
    public void testRecordEquality() {
        ReservationDTO dto1 = new ReservationDTO(
            1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "SFO", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 20),
            ReservationStatus.CONFIRMED, new BigDecimal("250.00"), new BigDecimal("50.00"),
            "Need GPS", 5L, LocalDateTime.of(2026, 1, 10, 10, 0),
            LocalDateTime.of(2026, 1, 10, 10, 0)
        );
        ReservationDTO dto2 = new ReservationDTO(
            1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
            "LAX", "SFO", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 20),
            ReservationStatus.CONFIRMED, new BigDecimal("250.00"), new BigDecimal("50.00"),
            "Need GPS", 5L, LocalDateTime.of(2026, 1, 10, 10, 0),
            LocalDateTime.of(2026, 1, 10, 10, 0)
        );

        assertEquals(dto1, dto2);
    }
}
