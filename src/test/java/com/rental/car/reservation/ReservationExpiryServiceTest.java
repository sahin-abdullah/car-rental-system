package com.rental.car.reservation;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationExpiryServiceTest {

    @Mock
    private ReservationRepository reservationRepo;

    private ReservationExpiryService expiryService;

    private Reservation expiredReservation;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        expiryService = new ReservationExpiryService(reservationRepo);

        expiredReservation = new Reservation(
                1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                ReservationStatus.PENDING, new BigDecimal("250.00"), new BigDecimal("50.00"),
                null, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(5), 0
        );
    }

    @Test
    public void testExpireStaleReservations() {
        when(reservationRepo.findExpiredPendingReservations(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(expiredReservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CANCELLED))
                .thenReturn(1);

        expiryService.expireStaleReservations();

        verify(reservationRepo, times(1)).findExpiredPendingReservations(any(LocalDateTime.class));
        verify(reservationRepo, times(1)).updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CANCELLED);
    }

    @Test
    public void testExpireStaleReservationsNoExpired() {
        when(reservationRepo.findExpiredPendingReservations(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        expiryService.expireStaleReservations();

        verify(reservationRepo, times(1)).findExpiredPendingReservations(any(LocalDateTime.class));
        verify(reservationRepo, never()).updateStatusAtomically(anyLong(), any(), any());
    }

    @Test
    public void testExpireStaleReservationsWithFailure() {
        Reservation reservation2 = new Reservation(
                2L, 11L, "customer2@example.com", "Jane Doe", "+1-555-0200",
                "SFO", "SFO", LocalDate.now().plusDays(3), LocalDate.now().plusDays(7),
                ReservationStatus.PENDING, new BigDecimal("300.00"), new BigDecimal("60.00"),
                null, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusMinutes(10), 0
        );

        List<Reservation> expiredList = Arrays.asList(expiredReservation, reservation2);
        when(reservationRepo.findExpiredPendingReservations(any(LocalDateTime.class)))
                .thenReturn(expiredList);
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CANCELLED))
                .thenReturn(1);
        when(reservationRepo.updateStatusAtomically(2L, ReservationStatus.PENDING, ReservationStatus.CANCELLED))
                .thenThrow(new RuntimeException("Database error"));

        expiryService.expireStaleReservations();

        verify(reservationRepo, times(1)).findExpiredPendingReservations(any(LocalDateTime.class));
        verify(reservationRepo, times(2)).updateStatusAtomically(anyLong(), any(), any());
    }
}
