package com.rental.car.reservation;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ReservationInternalControllerTest {

    @Mock
    private ReservationService service;

    private ReservationInternalController controller;

    private Reservation reservation;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ReservationInternalController(service);

        reservation = new Reservation(
                1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                ReservationStatus.CONFIRMED, new BigDecimal("250.00"), new BigDecimal("50.00"),
                "Need GPS", null, null, null, 0
        );
    }

    @Test
    public void testGetAllReservations() {
        Page<Reservation> page = new PageImpl<>(Arrays.asList(reservation));
        when(service.getAllReservations(any(Pageable.class))).thenReturn(page);

        Page<ReservationDTO> result = controller.getAllReservations(0, 20);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(service, times(1)).getAllReservations(any(Pageable.class));
    }

    @Test
    public void testGetReservationsByStatus() {
        Page<Reservation> page = new PageImpl<>(Arrays.asList(reservation));
        when(service.getReservationsByStatus(any(ReservationStatus.class), any(Pageable.class))).thenReturn(page);

        Page<ReservationDTO> result = controller.getReservationsByStatus(ReservationStatus.CONFIRMED, 0, 20);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(service, times(1)).getReservationsByStatus(any(ReservationStatus.class), any(Pageable.class));
    }

    @Test
    public void testConfirmReservation() {
        when(service.confirmReservation(1L)).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.confirmReservation(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).confirmReservation(1L);
    }

    @Test
    public void testStartReservation() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        when(service.startReservation(1L)).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.startReservation(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).startReservation(1L);
    }

    @Test
    public void testCompleteReservation() {
        reservation.setStatus(ReservationStatus.COMPLETED);
        when(service.completeReservation(1L)).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.completeReservation(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).completeReservation(1L);
    }

    @Test
    public void testGetReservation() {
        when(service.getReservationById(1L)).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.getReservation(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        assertEquals(result.getBody().id(), Long.valueOf(1L));
        verify(service, times(1)).getReservationById(1L);
    }
}
