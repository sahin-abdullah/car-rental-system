package com.rental.car.reservation;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ReservationControllerTest {

    @Mock
    private ReservationService service;

    private ReservationController controller;

    private Reservation reservation;
    private PriceCalculationResponse pricingResponse;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ReservationController(service);

        reservation = new Reservation(
                1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                ReservationStatus.CONFIRMED, new BigDecimal("250.00"), new BigDecimal("50.00"),
                "Need GPS", null, null, null, 0
        );

        pricingResponse = new PriceCalculationResponse(
                5L, new BigDecimal("50.00"),
                new PriceCalculationResponse.TimeCharge(0, 5, new BigDecimal("280.00"),
                        new BigDecimal("50.00"), new BigDecimal("250.00")),
                Arrays.asList(),
                new BigDecimal("250.00"),
                Arrays.asList(),
                new BigDecimal("25.00"),
                new BigDecimal("275.00"),
                true, "USD"
        );
    }

    @Test
    public void testCalculatePrice() {
        when(service.calculatePrice(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(pricingResponse);

        PriceCalculationResponse result = controller.calculatePrice(
                10L, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10)
        );

        assertNotNull(result);
        assertEquals(result.totalPrice(), new BigDecimal("275.00"));
        verify(service, times(1)).calculatePrice(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void testCreateReservation() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                "Need GPS"
        );

        when(service.createReservation(any(ReservationCreateRequest.class))).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.createReservation(request);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).createReservation(any(ReservationCreateRequest.class));
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

    @Test
    public void testUpdateReservation() {
        ReservationUpdateRequest request = new ReservationUpdateRequest(
                LocalDate.now().plusDays(6), LocalDate.now().plusDays(11), "Updated notes"
        );

        when(service.updateReservation(anyLong(), any(ReservationUpdateRequest.class))).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.updateReservation(1L, request);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).updateReservation(anyLong(), any(ReservationUpdateRequest.class));
    }

    @Test
    public void testCancelReservation() {
        when(service.cancelReservation(1L)).thenReturn(reservation);

        ResponseEntity<ReservationDTO> result = controller.cancelReservation(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        verify(service, times(1)).cancelReservation(1L);
    }
}
