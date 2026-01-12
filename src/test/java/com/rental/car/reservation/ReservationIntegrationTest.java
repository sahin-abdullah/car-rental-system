package com.rental.car.reservation;

import com.rental.car.TestcontainersConfiguration;
import com.rental.car.inventory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ReservationIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private InventoryService inventoryService;

    private String testBranchCode;
    private Long testCarId;

    @BeforeMethod
    public void setUp() {
        testBranchCode = "LAX" + (System.currentTimeMillis() % 10000);
        inventoryService.createBranchWithDetails(testBranchCode, "Test Branch", "+1-555-0100",
                "123 Test St", "Los Angeles", "CA", "USA", "90001");
        
        inventoryService.createCar(CarType.SEDAN, "RES" + System.currentTimeMillis(), 
                "Toyota", "Camry", 2024, testBranchCode, true);
        
        testCarId = inventoryService.searchWithFilters(
                null, testBranchCode, CarType.SEDAN, "Toyota", 2024, 
                null, null, 
                org.springframework.data.domain.PageRequest.of(0, 1))
                .getContent().get(0).id();
    }

    @Test
    public void testCreateReservation() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                testCarId, "customer@example.com", "John Doe", "+1-555-1234", 
                testBranchCode, testBranchCode, 
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), null
        );

        Reservation reservation = reservationService.createReservation(request);

        assertNotNull(reservation);
        assertEquals(reservation.getStatus(), ReservationStatus.PENDING);
    }

    @Test
    public void testReservationStatusTransitions() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                testCarId, "customer@example.com", "John Doe", "+1-555-1234",
                testBranchCode, testBranchCode,
                LocalDate.now().minusDays(3), LocalDate.now(), null
        );
        Reservation reservation = reservationService.createReservation(request);

        reservationService.confirmReservation(reservation.getId());
        Reservation confirmed = reservationService.getReservationById(reservation.getId());
        assertEquals(confirmed.getStatus(), ReservationStatus.CONFIRMED);

        reservationService.startReservation(reservation.getId());
        Reservation active = reservationService.getReservationById(reservation.getId());
        assertEquals(active.getStatus(), ReservationStatus.ACTIVE);

        reservationService.completeReservation(reservation.getId());
        Reservation completed = reservationService.getReservationById(reservation.getId());
        assertEquals(completed.getStatus(), ReservationStatus.COMPLETED);
    }

    @Test
    public void testCancelReservation() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                testCarId, "customer@example.com", "John Doe", "+1-555-1234",
                testBranchCode, testBranchCode,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), null
        );
        Reservation reservation = reservationService.createReservation(request);

        reservationService.cancelReservation(reservation.getId());
        Reservation cancelled = reservationService.getReservationById(reservation.getId());
        assertEquals(cancelled.getStatus(), ReservationStatus.CANCELLED);
    }
}
