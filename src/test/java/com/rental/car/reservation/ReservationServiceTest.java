package com.rental.car.reservation;

import com.rental.car.exceptions.BusinessRuleViolationException;
import com.rental.car.exceptions.ResourceNotFoundException;
import com.rental.car.exceptions.ReservationConflictException;
import com.rental.car.inventory.CarDTO;
import com.rental.car.inventory.CarType;
import com.rental.car.inventory.InventoryService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepo;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private PricingService pricingService;

    private ReservationService reservationService;

    private CarDTO carDTO;
    private Reservation reservation;
    private PriceCalculationResponse pricingResponse;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(reservationRepo, inventoryService, pricingService);

        carDTO = new CarDTO(10L, CarType.SEDAN, "ABC123", "Toyota", "Camry",
                2023, "LAX", "LAX Branch", "Los Angeles", true, null);

        reservation = new Reservation(
                1L, 10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                ReservationStatus.PENDING, new BigDecimal("250.00"), new BigDecimal("50.00"),
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
    public void testGetReservationById() {
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(result.getId(), Long.valueOf(1L));
        verify(reservationRepo, times(1)).findById(1L);
    }

    @Test
    public void testGetCustomerReservations() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> page = new PageImpl<>(Arrays.asList(reservation));

        when(reservationRepo.findByCustomerEmailOrderByCreatedAtDesc("customer@example.com", pageable))
                .thenReturn(page);

        Page<Reservation> result = reservationService.getCustomerReservations("customer@example.com", pageable);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(reservationRepo, times(1)).findByCustomerEmailOrderByCreatedAtDesc("customer@example.com", pageable);
    }

    @Test
    public void testGetAllReservations() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Reservation> page = new PageImpl<>(Arrays.asList(reservation));

        when(reservationRepo.findAll(pageable)).thenReturn(page);

        Page<Reservation> result = reservationService.getAllReservations(pageable);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(reservationRepo, times(1)).findAll(pageable);
    }

    @Test
    public void testGetReservationsByStatus() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Reservation> page = new PageImpl<>(Arrays.asList(reservation));

        when(reservationRepo.findByStatus(ReservationStatus.PENDING, pageable)).thenReturn(page);

        Page<Reservation> result = reservationService.getReservationsByStatus(ReservationStatus.PENDING, pageable);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        verify(reservationRepo, times(1)).findByStatus(ReservationStatus.PENDING, pageable);
    }

    @Test
    public void testCalculatePrice() {
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO));
        when(reservationRepo.hasConflictingReservation(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(false);
        when(pricingService.calculatePrice(any(), anyString(), anyString(), any(), any(), anyBoolean()))
                .thenReturn(pricingResponse);

        PriceCalculationResponse result = reservationService.calculatePrice(
                10L, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null, null
        );

        assertNotNull(result);
        assertEquals(result.totalPrice(), new BigDecimal("275.00"));
        verify(inventoryService, times(1)).getCarById(10L);
        verify(pricingService, times(1)).calculatePrice(any(), anyString(), anyString(), any(), any(), anyBoolean());
    }

    @Test
    public void testCreateReservation() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                "Need GPS"
        );

        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO));
        when(inventoryService.isValidBranch("LAX")).thenReturn(true);
        when(reservationRepo.hasConflictingReservation(anyLong(), any(), any())).thenReturn(false);
        when(pricingService.calculatePrice(any(), anyString(), anyString(), any(), any(), anyBoolean()))
                .thenReturn(pricingResponse);
        when(reservationRepo.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.createReservation(request);

        assertNotNull(result);
        verify(inventoryService, times(1)).getCarById(10L);
        verify(reservationRepo, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testConfirmReservation() {
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
                .thenReturn(1);
        when(reservationRepo.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.confirmReservation(1L);

        assertNotNull(result);
        verify(reservationRepo, times(1)).updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
    }

    @Test
    public void testCancelReservation() {
        reservation.setPickupDate(LocalDate.now().plusDays(5));
        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CANCELLED))
                .thenReturn(1);

        Reservation result = reservationService.cancelReservation(1L);

        assertNotNull(result);
        verify(reservationRepo, times(1)).updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CANCELLED);
    }

    // Additional error path tests for better coverage

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetReservationByIdNotFound() {
        when(reservationRepo.findById(999L)).thenReturn(Optional.empty());
        reservationService.getReservationById(999L);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCalculatePriceCarNotFound() {
        when(inventoryService.getCarById(999L)).thenReturn(Optional.empty());
        reservationService.calculatePrice(999L, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null, null);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateReservationCarNotFound() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                999L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(999L)).thenReturn(Optional.empty());
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = ReservationConflictException.class)
    public void testCreateReservationCarNotAvailable() {
        CarDTO unavailableCar = new CarDTO(10L, CarType.SEDAN, "ABC123", "Toyota", "Camry",
                2023, "LAX", "LAX Branch", "Los Angeles", false, null);
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(unavailableCar));
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateReservationInvalidPickupBranch() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "INVALID", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO));
        when(inventoryService.isValidBranch("INVALID")).thenReturn(false);
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateReservationInvalidReturnBranch() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "INVALID", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO));
        when(inventoryService.isValidBranch("LAX")).thenReturn(true);
        when(inventoryService.isValidBranch("INVALID")).thenReturn(false);
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateReservationCarAtWrongBranch() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "SFO", "SFO", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO)); // Car is at LAX
        when(inventoryService.isValidBranch("SFO")).thenReturn(true);
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = ReservationConflictException.class)
    public void testCreateReservationConflicting() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO));
        when(inventoryService.isValidBranch("LAX")).thenReturn(true);
        when(reservationRepo.hasConflictingReservation(anyLong(), any(), any())).thenReturn(true);
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = ReservationConflictException.class)
    public void testCreateReservationDatabaseConstraintViolation() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L, "customer@example.com", "John Doe", "+1-555-0100",
                "LAX", "LAX", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), null
        );
        when(inventoryService.getCarById(10L)).thenReturn(Optional.of(carDTO));
        when(inventoryService.isValidBranch("LAX")).thenReturn(true);
        when(reservationRepo.hasConflictingReservation(anyLong(), any(), any())).thenReturn(false);
        when(pricingService.calculatePrice(any(), anyString(), anyString(), any(), any(), anyBoolean()))
                .thenReturn(pricingResponse);
        when(reservationRepo.save(any(Reservation.class)))
                .thenThrow(new DataIntegrityViolationException("reservations_no_overlap_per_car"));
        reservationService.createReservation(request);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testUpdateReservationInActiveStatus() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        ReservationUpdateRequest request = new ReservationUpdateRequest(
                LocalDate.now().plusDays(6), LocalDate.now().plusDays(11), null
        );
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.updateReservation(1L, request);
    }

    @Test(expectedExceptions = ReservationConflictException.class)
    public void testUpdateReservationDateConflict() {
        reservation.setStatus(ReservationStatus.PENDING);
        ReservationUpdateRequest request = new ReservationUpdateRequest(
                LocalDate.now().plusDays(6), LocalDate.now().plusDays(11), null
        );
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.hasConflictingReservationExcluding(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        reservationService.updateReservation(1L, request);
    }

    @Test
    public void testUpdateReservationOnlyNotes() {
        reservation.setStatus(ReservationStatus.PENDING);
        ReservationUpdateRequest request = new ReservationUpdateRequest(null, null, "Updated notes");
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.updateReservation(1L, request);

        assertNotNull(result);
        verify(reservationRepo, never()).hasConflictingReservationExcluding(anyLong(), anyLong(), any(), any());
        verify(reservationRepo, times(1)).save(any(Reservation.class));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testConfirmExpiredReservation() {
        reservation.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.confirmReservation(1L);
    }

    @Test
    public void testConfirmAlreadyConfirmed() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
                .thenReturn(0);

        Reservation result = reservationService.confirmReservation(1L);

        assertNotNull(result);
        assertEquals(result.getStatus(), ReservationStatus.CONFIRMED);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testConfirmCancelledReservation() {
        reservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
                .thenReturn(0);
        reservationService.confirmReservation(1L);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testStartReservationBeforePickupDate() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPickupDate(LocalDate.now().plusDays(2));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.startReservation(1L);
    }

    @Test
    public void testStartReservationAlreadyActive() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setPickupDate(LocalDate.now().minusDays(1));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE))
                .thenReturn(0);
        doNothing().when(inventoryService).updateCarAvailability(10L, false);

        Reservation result = reservationService.startReservation(1L);

        assertNotNull(result);
        verify(inventoryService, times(1)).updateCarAvailability(10L, false);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testStartReservationWrongStatus() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPickupDate(LocalDate.now().minusDays(1));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE))
                .thenReturn(0);
        reservationService.startReservation(1L);
    }

    @Test
    public void testStartReservationSuccess() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPickupDate(LocalDate.now().minusDays(1));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE))
                .thenReturn(1);
        doNothing().when(inventoryService).updateCarAvailability(10L, false);

        Reservation result = reservationService.startReservation(1L);

        assertNotNull(result);
        verify(inventoryService, times(1)).updateCarAvailability(10L, false);
        verify(reservationRepo, times(2)).findById(1L);
    }

    @Test
    public void testCompleteReservationAlreadyCompleted() {
        reservation.setStatus(ReservationStatus.COMPLETED);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.completeReservation(1L);

        assertNotNull(result);
        assertEquals(result.getStatus(), ReservationStatus.COMPLETED);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testCompleteReservationWrongStatus() {
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.completeReservation(1L);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testCompleteReservationBeforeReturnDate() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReturnDate(LocalDate.now().plusDays(2));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.completeReservation(1L);
    }

    @Test
    public void testCompleteReservationSuccess() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReturnDate(LocalDate.now().minusDays(1));
        reservation.setPickupBranchCode("LAX");
        reservation.setReturnBranchCode("LAX");
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.ACTIVE, ReservationStatus.COMPLETED))
                .thenReturn(1);
        doNothing().when(inventoryService).updateCarAvailability(10L, true);

        Reservation result = reservationService.completeReservation(1L);

        assertNotNull(result);
        verify(inventoryService, times(1)).updateCarAvailability(10L, true);
        verify(inventoryService, never()).moveCarToBranch(anyLong(), anyString());
    }

    @Test
    public void testCompleteReservationWithBranchMove() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReturnDate(LocalDate.now().minusDays(1));
        reservation.setPickupBranchCode("LAX");
        reservation.setReturnBranchCode("SFO");
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.ACTIVE, ReservationStatus.COMPLETED))
                .thenReturn(1);
        doNothing().when(inventoryService).updateCarAvailability(10L, true);
        doNothing().when(inventoryService).moveCarToBranch(10L, "SFO");

        Reservation result = reservationService.completeReservation(1L);

        assertNotNull(result);
        verify(inventoryService, times(1)).updateCarAvailability(10L, true);
        verify(inventoryService, times(1)).moveCarToBranch(10L, "SFO");
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testCancelCompletedReservation() {
        reservation.setStatus(ReservationStatus.COMPLETED);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.cancelReservation(1L);
    }

    @Test
    public void testCancelAlreadyCancelled() {
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setPickupDate(LocalDate.now().plusDays(5));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.cancelReservation(1L);

        assertNotNull(result);
        assertEquals(result.getStatus(), ReservationStatus.CANCELLED);
    }

    @Test(expectedExceptions = BusinessRuleViolationException.class)
    public void testCancelWithin24Hours() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPickupDate(LocalDate.now());
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.cancelReservation(1L);
    }

    @Test
    public void testCancelActiveReservation() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setPickupDate(LocalDate.now().minusDays(1));
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepo.updateStatusAtomically(1L, ReservationStatus.ACTIVE, ReservationStatus.CANCELLED))
                .thenReturn(1);
        doNothing().when(inventoryService).updateCarAvailability(10L, true);

        Reservation result = reservationService.cancelReservation(1L);

        assertNotNull(result);
        verify(inventoryService, times(1)).updateCarAvailability(10L, true);
    }

    @Test
    public void testGetUpcomingReservations() {
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepo.findUpcomingReservationsForCustomer("customer@example.com", LocalDate.now()))
                .thenReturn(reservations);

        List<Reservation> result = reservationService.getUpcomingReservations("customer@example.com");

        assertNotNull(result);
        assertEquals(result.size(), 1);
        verify(reservationRepo, times(1)).findUpcomingReservationsForCustomer("customer@example.com", LocalDate.now());
    }
}
