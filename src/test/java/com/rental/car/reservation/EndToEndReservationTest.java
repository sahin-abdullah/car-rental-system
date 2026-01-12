package com.rental.car.reservation;

import com.rental.car.TestcontainersConfiguration;
import com.rental.car.inventory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class EndToEndReservationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private InventoryService inventoryService;

    private String laxBranchCode;
    private String sfoBranchCode;
    private Long sedanCarId;
    private Long suvCarId;

    @BeforeMethod
    public void setUp() {
        long suffix = System.currentTimeMillis() % 10000;
        laxBranchCode = "LAX" + suffix;
        sfoBranchCode = "SFO" + suffix;

        inventoryService.createBranchWithDetails(laxBranchCode, "LAX Branch", "+1-555-0100",
                "123 LAX St", "Los Angeles", "CA", "USA", "90001");
        inventoryService.createBranchWithDetails(sfoBranchCode, "SFO Branch", "+1-555-0200",
                "456 SFO St", "San Francisco", "CA", "USA", "94102");

        long timestamp = System.currentTimeMillis();
        inventoryService.createCar(CarType.SEDAN, "SEDAN-" + timestamp,
                "Toyota", "Camry", 2024, laxBranchCode, true);
        inventoryService.createCar(CarType.SUV, "SUV-" + timestamp,
                "Toyota", "RAV4", 2024, laxBranchCode, true);


        sedanCarId = inventoryService.searchWithFilters(
                null, laxBranchCode, CarType.SEDAN, "Toyota", 2024,
                null, null, PageRequest.of(0, 1))
                .getContent().get(0).id();
        
        suvCarId = inventoryService.searchWithFilters(
                null, laxBranchCode, CarType.SUV, "Toyota", 2024,
                null, null, PageRequest.of(0, 1))
                .getContent().get(0).id();
    }

    @Test
    public void testCompleteRentalWorkflow() {
        // Customer searches for available cars
        var availableCars = inventoryService.searchWithFilters(
                null, laxBranchCode, CarType.SEDAN, null, null,
                null, null,
                PageRequest.of(0, 10));

        assertTrue(availableCars.getTotalElements() > 0);

        // Customer creates reservation
        ReservationCreateRequest request = new ReservationCreateRequest(
                sedanCarId, "customer@example.com", "John Doe", "+1-555-1234",
                laxBranchCode, laxBranchCode,
                LocalDate.now().minusDays(2), LocalDate.now(), null
        );
        Reservation reservation = reservationService.createReservation(request);
        assertEquals(reservation.getStatus(), ReservationStatus.PENDING);

        // System confirms reservation (payment processed)
        Reservation confirmed = reservationService.confirmReservation(reservation.getId());
        assertEquals(confirmed.getStatus(), ReservationStatus.CONFIRMED);

        // Customer picks up car
        Reservation active = reservationService.startReservation(reservation.getId());
        assertEquals(active.getStatus(), ReservationStatus.ACTIVE);

        // Verify car is now unavailable through DTO
        CarDTO car = inventoryService.getCarById(sedanCarId).orElseThrow();
        assertFalse(car.available());

        // Customer returns car
        Reservation completed = reservationService.completeReservation(reservation.getId());
        assertEquals(completed.getStatus(), ReservationStatus.COMPLETED);

        // Verify car is available again through DTO
        CarDTO returnedCar = inventoryService.getCarById(sedanCarId).orElseThrow();
        assertTrue(returnedCar.available());
    }

    @Test
    public void testOneWayRental() {
        // Customer creates one-way rental from LAX to SFO
        ReservationCreateRequest request = new ReservationCreateRequest(
                sedanCarId, "customer@example.com", "Jane Smith", "+1-555-5678",
                laxBranchCode, sfoBranchCode,
                LocalDate.now().minusDays(2), LocalDate.now(), "One-way rental to SFO"
        );
        Reservation reservation = reservationService.createReservation(request);

        // Complete full workflow
        reservationService.confirmReservation(reservation.getId());
        reservationService.startReservation(reservation.getId());
        reservationService.completeReservation(reservation.getId());

        // Verify car moved to return branch through DTO
        CarDTO car = inventoryService.getCarById(sedanCarId).orElseThrow();
        assertEquals(car.branchCode(), sfoBranchCode);
    }

    @Test
    public void testCustomerCancelsBeforePickup() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                sedanCarId, "customer@example.com", "John Doe", "+1-555-1234",
                laxBranchCode, laxBranchCode,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), null
        );
        Reservation reservation = reservationService.createReservation(request);
        reservationService.confirmReservation(reservation.getId());

        // Customer cancels before pickup
        Reservation cancelled = reservationService.cancelReservation(reservation.getId());
        assertEquals(cancelled.getStatus(), ReservationStatus.CANCELLED);

        // Verify car is still available through DTO
        CarDTO car = inventoryService.getCarById(sedanCarId).orElseThrow();
        assertTrue(car.available());
    }

    @Test
    public void testBackToBackReservations() {
        LocalDate firstStart = LocalDate.now();
        LocalDate firstEnd = LocalDate.now().plusDays(2);
        LocalDate secondStart = LocalDate.now().plusDays(2);
        LocalDate secondEnd = LocalDate.now().plusDays(4);

        // First reservation
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                sedanCarId, "customer1@example.com", "John Doe", "+1-555-1111",
                laxBranchCode, laxBranchCode,
                firstStart, firstEnd, null
        );
        Reservation res1 = reservationService.createReservation(request1);

        // Second reservation (back-to-back)
        ReservationCreateRequest request2 = new ReservationCreateRequest(
                sedanCarId, "customer2@example.com", "Jane Smith", "+1-555-2222",
                laxBranchCode, laxBranchCode,
                secondStart, secondEnd, null
        );
        Reservation res2 = reservationService.createReservation(request2);

        assertNotNull(res1);
        assertNotNull(res2);
        assertEquals(res1.getStatus(), ReservationStatus.PENDING);
        assertEquals(res2.getStatus(), ReservationStatus.PENDING);
    }

    @Test
    public void testConcurrentReservationAttempts() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(12);

        // Multiple users try to book the same car for same dates
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    ReservationCreateRequest request = new ReservationCreateRequest(
                            suvCarId,
                            "customer" + threadNum + "@example.com",
                            "Customer " + threadNum,
                            "+1-555-" + String.format("%04d", threadNum),
                            laxBranchCode,
                            laxBranchCode,
                            startDate,
                            endDate,
                            null
                    );
                    reservationService.createReservation(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(successCount.get(), 1, "Only one concurrent reservation should succeed");
        assertEquals(failureCount.get(), threadCount - 1, "All other reservations should fail");
    }

    @Test
    public void testConcurrentStatusUpdates() throws InterruptedException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                sedanCarId, "concurrent@example.com", "Concurrent Test", "+1-555-CONC",
                laxBranchCode, laxBranchCode,
                LocalDate.now(), LocalDate.now().plusDays(2), null
        );
        Reservation reservation = reservationService.createReservation(request);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Multiple threads try to confirm the same reservation
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    reservationService.confirmReservation(reservation.getId());
                } catch (Exception e) {
                    // Expected for concurrent attempts
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify final state is CONFIRMED
        Reservation updated = reservationService.getReservationById(reservation.getId());
        assertEquals(updated.getStatus(), ReservationStatus.CONFIRMED);
    }

    @Test
    public void testReservationPricing() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                sedanCarId, "customer@example.com", "John Doe", "+1-555-1234",
                laxBranchCode, laxBranchCode,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), null
        );
        Reservation reservation = reservationService.createReservation(request);

        assertNotNull(reservation.getTotalPrice());
        assertTrue(reservation.getTotalPrice().doubleValue() > 0);
    }

    @Test
    public void testMultipleCustomerReservations() {
        // Customer 1 makes reservation
        ReservationCreateRequest req1 = new ReservationCreateRequest(
                sedanCarId, "customer1@example.com", "John Doe", "+1-555-1111",
                laxBranchCode, laxBranchCode,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), null
        );
        
        reservationService.createReservation(req1);

        // Customer 2 makes reservation for different car
        ReservationCreateRequest req2 = new ReservationCreateRequest(
                suvCarId, "customer2@example.com", "Jane Smith", "+1-555-2222",
                laxBranchCode, laxBranchCode,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), null
        );

        reservationService.createReservation(req2);

        // Verify both cars are tracked correctly through DTOs
        CarDTO sedan = inventoryService.getCarById(sedanCarId).orElseThrow();
        CarDTO suv = inventoryService.getCarById(suvCarId).orElseThrow();
        
        assertTrue(sedan.available());
        assertTrue(suv.available());
    }
}
