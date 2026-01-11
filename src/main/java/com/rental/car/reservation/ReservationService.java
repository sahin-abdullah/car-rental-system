package com.rental.car.reservation;

import com.rental.car.exceptions.BusinessRuleViolationException;
import com.rental.car.exceptions.ResourceNotFoundException;
import com.rental.car.exceptions.ReservationConflictException;
import com.rental.car.inventory.CarDTO;
import com.rental.car.inventory.InventoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final InventoryService inventoryService;
    private final PricingService pricingService;

    public ReservationService(
            ReservationRepository reservationRepo, 
            InventoryService inventoryService,
            PricingService pricingService) {
        this.reservationRepo = reservationRepo;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Reservation createReservation(ReservationCreateRequest request) {
        // 1. Validate car exists and is available
        CarDTO car = inventoryService.getCarById(request.carId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + request.carId()));

        if (!car.available()) {
            throw new ReservationConflictException("Car is not available for rent");
        }

        // 2. Validate branches exist
        if (!inventoryService.isValidBranch(request.pickupBranchCode())) {
            throw new ResourceNotFoundException("Pickup branch not found: " + request.pickupBranchCode());
        }
        if (!inventoryService.isValidBranch(request.returnBranchCode())) {
            throw new ResourceNotFoundException("Return branch not found: " + request.returnBranchCode());
        }

        // 3. Validate car is at the pickup branch
        if (!car.branchCode().equals(request.pickupBranchCode())) {
            throw new IllegalArgumentException(
                String.format("Car is not available at pickup branch %s. Car is currently at branch %s",
                    request.pickupBranchCode(), car.branchCode())
            );
        }

        // 4. Check for conflicting reservations (best-effort check before DB constraint)
        boolean available = !reservationRepo.hasConflictingReservation(
                request.carId(), request.pickupDate(), request.returnDate());
        
        if (!available) {
            throw new ReservationConflictException(
                "Car is already reserved for the selected dates");
        }

        // 5. Calculate pricing using PricingService
        PriceCalculationResponse pricing = pricingService.calculatePrice(
            car.type(), 
            request.pickupBranchCode(),
            request.returnBranchCode(),
            request.pickupDate(), 
            request.returnDate(),
            available
        );

        // 5. Create reservation with 30-minute expiry for PENDING status
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        Reservation reservation = new Reservation(
            null,
            request.carId(),
            request.customerEmail(),
            request.customerName(),
            request.customerPhone(),
            request.pickupBranchCode(),
            request.returnBranchCode(),
            request.pickupDate(),
            request.returnDate(),
            ReservationStatus.PENDING,
            pricing.totalPrice(),
            pricing.dailyRate(),
            request.notes(),
            null,
            null,
            expiresAt,
            null
        );

        try {
            return reservationRepo.save(reservation);
        } catch (DataIntegrityViolationException ex) {
            // DB exclusion constraint rejected overlapping reservation
            if (ex.getMessage() != null && ex.getMessage().contains("reservations_no_overlap_per_car")) {
                throw new ReservationConflictException(
                    "Car is already reserved for the selected dates");
            }
            throw ex; // Re-throw if it's a different constraint violation
        }
    }

    @Transactional(readOnly = true)
    public PriceCalculationResponse calculatePrice(Long carId, LocalDate pickupDate, LocalDate returnDate) {
        // Verify car exists and get its type
        CarDTO car = inventoryService.getCarById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));

        // Get pickup branch from car's current location
        String pickupBranchCode = car.branchCode();
        
        // Assume same branch return for price calculation (user can specify different in actual reservation)
        String returnBranchCode = pickupBranchCode;

        // Check availability
        boolean available = !reservationRepo.hasConflictingReservation(carId, pickupDate, returnDate);

        // Use PricingService to calculate price with CarType
        return pricingService.calculatePrice(car.type(), pickupBranchCode, returnBranchCode, pickupDate, returnDate, available);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Reservation updateReservation(Long reservationId, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));

        // Only allow updates for PENDING or CONFIRMED status
        if (reservation.getStatus() == ReservationStatus.ACTIVE ||
            reservation.getStatus() == ReservationStatus.COMPLETED ||
            reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleViolationException("Cannot update reservation in " + reservation.getStatus() + " status");
        }

        // Update dates if provided
        LocalDate newPickupDate = request.pickupDate() != null ? request.pickupDate() : reservation.getPickupDate();
        LocalDate newReturnDate = request.returnDate() != null ? request.returnDate() : reservation.getReturnDate();

        // Check for conflicts if dates changed
        if (!newPickupDate.equals(reservation.getPickupDate()) || !newReturnDate.equals(reservation.getReturnDate())) {
            if (reservationRepo.hasConflictingReservationExcluding(
                    reservation.getCarId(), reservationId, newPickupDate, newReturnDate)) {
                throw new ReservationConflictException("Car is already reserved for the new dates");
            }

            // Get car type for pricing calculation
            CarDTO car = inventoryService.getCarById(reservation.getCarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + reservation.getCarId()));

            // Recalculate price with updated dates
            boolean available = !reservationRepo.hasConflictingReservationExcluding(
                    reservation.getCarId(), reservationId, newPickupDate, newReturnDate);
            
            PriceCalculationResponse pricing = pricingService.calculatePrice(
                car.type(),
                reservation.getPickupBranchCode(),
                reservation.getReturnBranchCode(),
                newPickupDate, 
                newReturnDate,
                available
            );

            reservation.setPickupDate(newPickupDate);
            reservation.setReturnDate(newReturnDate);
            reservation.setTotalPrice(pricing.totalPrice());
            reservation.setDailyRate(pricing.dailyRate());
        }

        // Update notes if provided
        if (request.notes() != null) {
            reservation.setNotes(request.notes());
        }

        return reservationRepo.save(reservation);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Reservation confirmReservation(Long reservationId) {
        // Check if reservation exists and is not expired
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
        
        if (reservation.isExpired()) {
            throw new IllegalStateException(
                "Reservation has expired. Please create a new reservation."
            );
        }

        // Atomic status update: only succeeds if current status is PENDING
        int updated = reservationRepo.updateStatusAtomically(
            reservationId, 
            ReservationStatus.PENDING, 
            ReservationStatus.CONFIRMED
        );

        if (updated == 0) {
            // Either reservation doesn't exist or status is not PENDING
            Reservation res = reservationRepo.findById(reservationId)
                    .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
            
            if (res.getStatus() == ReservationStatus.CONFIRMED) {
                // Already confirmed - idempotent success
                return res;
            }
            
            throw new BusinessRuleViolationException(
                "Only PENDING reservations can be confirmed. Current status: " + res.getStatus()
            );
        }

        // Fetch the reservation and clear expiry
        reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
        reservation.setExpiresAt(null);
        return reservationRepo.save(reservation);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Reservation startReservation(Long reservationId) {
        // First fetch the reservation to get car ID (needed for inventory update)
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));

        // Validate pickup date - can't start reservation before pickup date
        if (LocalDate.now().isBefore(reservation.getPickupDate())) {
            throw new BusinessRuleViolationException(
                "Cannot start reservation before pickup date: " + reservation.getPickupDate()
            );
        }

        // Atomic status update: only succeeds if current status is CONFIRMED
        int updated = reservationRepo.updateStatusAtomically(
            reservationId, 
            ReservationStatus.CONFIRMED, 
            ReservationStatus.ACTIVE
        );

        if (updated == 0) {
            // Re-fetch to get current status
            reservation = reservationRepo.findById(reservationId)
                    .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
            
            if (reservation.getStatus() == ReservationStatus.ACTIVE) {
                // Already started - idempotent success, but ensure car is marked unavailable
                inventoryService.updateCarAvailability(reservation.getCarId(), false);
                return reservation;
            }
            
            throw new BusinessRuleViolationException(
                "Only CONFIRMED reservations can be started. Current status: " + reservation.getStatus()
            );
        }
        
        // Mark car as unavailable in inventory
        inventoryService.updateCarAvailability(reservation.getCarId(), false);
        
        // Fetch and return the updated reservation
        return reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Reservation completeReservation(Long reservationId) {
        // First fetch the reservation to validate and get car details
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));

        // Validate current status
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                // Already completed - idempotent success
                return reservation;
            }
            throw new BusinessRuleViolationException(
                "Only ACTIVE reservations can be completed. Current status: " + reservation.getStatus()
            );
        }

        // Prevent completing reservation before return date
        if (LocalDate.now().isBefore(reservation.getReturnDate())) {
            throw new BusinessRuleViolationException(
                "Cannot complete reservation before return date: " + reservation.getReturnDate()
            );
        }

        // Atomic status update: only succeeds if current status is ACTIVE
        int updated = reservationRepo.updateStatusAtomically(
            reservationId, 
            ReservationStatus.ACTIVE, 
            ReservationStatus.COMPLETED
        );

        if (updated == 0) {
            // Re-fetch to get current status
            reservation = reservationRepo.findById(reservationId)
                    .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
            
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                // Already completed by another transaction - idempotent success
                return reservation;
            }
            
            throw new BusinessRuleViolationException(
                "Reservation status changed during processing. Current status: " + reservation.getStatus()
            );
        }
        
        // Mark car as available again (if at return branch)
        inventoryService.updateCarAvailability(reservation.getCarId(), true);
        
        // If returned to different branch, move the car
        if (!reservation.getPickupBranchCode().equals(reservation.getReturnBranchCode())) {
            inventoryService.moveCarToBranch(reservation.getCarId(), reservation.getReturnBranchCode());
        }
        
        // Fetch and return the updated reservation
        return reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));

        ReservationStatus currentStatus = reservation.getStatus();
        
        if (currentStatus == ReservationStatus.COMPLETED) {
            throw new BusinessRuleViolationException("Cannot cancel a completed reservation");
        }
        
        if (currentStatus == ReservationStatus.CANCELLED) {
            // Already cancelled - idempotent success
            return reservation;
        }

        // Enforce cancellation window - must cancel at least 24 hours before pickup
        if (currentStatus != ReservationStatus.ACTIVE) {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            if (reservation.getPickupDate().isBefore(tomorrow)) {
                throw new BusinessRuleViolationException(
                    "Cannot cancel reservation within 24 hours of pickup date. " +
                    "Pickup date: " + reservation.getPickupDate()
                );
            }
        }

        // For ACTIVE reservations, we need to make car available again
        boolean wasActive = (currentStatus == ReservationStatus.ACTIVE);
        
        // Try to atomically update status to CANCELLED from current status
        int updated = reservationRepo.updateStatusAtomically(
            reservationId, 
            currentStatus, 
            ReservationStatus.CANCELLED
        );

        if (updated == 0) {
            // Status changed during processing, re-fetch and check
            reservation = reservationRepo.findById(reservationId)
                    .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
            
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                // Already cancelled by another transaction - idempotent success
                if (wasActive) {
                    inventoryService.updateCarAvailability(reservation.getCarId(), true);
                }
                return reservation;
            }
            
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                throw new BusinessRuleViolationException("Cannot cancel a completed reservation");
            }
            
            throw new BusinessRuleViolationException(
                "Reservation status changed during processing. Current status: " + reservation.getStatus()
            );
        }

        // If it was active, make car available again
        if (wasActive) {
            inventoryService.updateCarAvailability(reservation.getCarId(), true);
        }

        // Fetch and return the updated reservation
        return reservationRepo.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));
    }

    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        return reservationRepo.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.reservation(id));
    }

    @Transactional(readOnly = true)
    public Page<Reservation> getCustomerReservations(String customerEmail, Pageable pageable) {
        return reservationRepo.findByCustomerEmailOrderByCreatedAtDesc(customerEmail, pageable);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getUpcomingReservations(String customerEmail) {
        return reservationRepo.findUpcomingReservationsForCustomer(customerEmail, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Reservation> getReservationsByStatus(ReservationStatus status, Pageable pageable) {
        return reservationRepo.findByStatus(status, pageable);
    }
}
