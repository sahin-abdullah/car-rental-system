package com.rental.car.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task to automatically expire PENDING reservations that have exceeded their timeout.
 * Runs every 5 minutes to keep inventory available.
 */
@Service
class ReservationExpiryService {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryService.class);

    private final ReservationRepository reservationRepo;

    ReservationExpiryService(ReservationRepository reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void expireStaleReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find all PENDING reservations that have passed their expiry time
        List<Reservation> expiredReservations = reservationRepo.findExpiredPendingReservations(now);
        
        if (expiredReservations.isEmpty()) {
            log.debug("No expired PENDING reservations found");
            return;
        }
        
        log.info("Found {} expired PENDING reservations, auto-cancelling...", expiredReservations.size());
        
        int cancelledCount = 0;
        for (Reservation reservation : expiredReservations) {
            try {
                int updated = reservationRepo.updateStatusAtomically(
                    reservation.getId(),
                    ReservationStatus.PENDING,
                    ReservationStatus.CANCELLED
                );
                
                if (updated > 0) {
                    cancelledCount++;
                    log.debug("Auto-cancelled expired reservation ID: {} (customer: {}, expired at: {})",
                        reservation.getId(), 
                        reservation.getCustomerEmail(), 
                        reservation.getExpiresAt());
                }
            } catch (Exception e) {
                log.error("Failed to cancel expired reservation ID: {}", reservation.getId(), e);
            }
        }
        
        log.info("Successfully auto-cancelled {} out of {} expired PENDING reservations", 
            cancelledCount, expiredReservations.size());
    }
}
