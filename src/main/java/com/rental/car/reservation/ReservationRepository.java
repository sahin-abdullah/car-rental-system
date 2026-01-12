package com.rental.car.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r 
        WHERE r.carId = :carId 
        AND r.status NOT IN ('CANCELLED', 'COMPLETED')
        AND r.pickupDate < :returnDate 
        AND r.returnDate > :pickupDate
    """)
    boolean hasConflictingReservation(
        @Param("carId") Long carId,
        @Param("pickupDate") LocalDate pickupDate,
        @Param("returnDate") LocalDate returnDate
    );

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r 
        WHERE r.carId = :carId 
        AND r.id != :reservationId
        AND r.status NOT IN ('CANCELLED', 'COMPLETED')
        AND r.pickupDate < :returnDate 
        AND r.returnDate > :pickupDate
    """)
    boolean hasConflictingReservationExcluding(
        @Param("carId") Long carId,
        @Param("reservationId") Long reservationId,
        @Param("pickupDate") LocalDate pickupDate,
        @Param("returnDate") LocalDate returnDate
    );

    Page<Reservation> findByCarId(Long carId, Pageable pageable);

    Page<Reservation> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail, Pageable pageable);

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    @Query("""
        SELECT r FROM Reservation r 
        WHERE r.customerEmail = :email 
        AND r.pickupDate >= :today
        AND r.status NOT IN ('CANCELLED', 'COMPLETED')
        ORDER BY r.pickupDate ASC
    """)
    List<Reservation> findUpcomingReservationsForCustomer(
        @Param("email") String email,
        @Param("today") LocalDate today
    );

    List<Reservation> findByStatusAndReturnDateBefore(ReservationStatus status, LocalDate date);

    @Query("""
        SELECT r FROM Reservation r 
        WHERE (r.pickupBranchCode = :branchCode OR r.returnBranchCode = :branchCode)
        AND r.pickupDate <= :endDate 
        AND r.returnDate >= :startDate
        AND r.status NOT IN ('CANCELLED')
        ORDER BY r.pickupDate ASC
    """)
    List<Reservation> findByBranchAndDateRange(
        @Param("branchCode") String branchCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    long countByCarIdAndStatusIn(Long carId, List<ReservationStatus> statuses);

    boolean existsByCustomerEmailAndStatusIn(String email, List<ReservationStatus> statuses);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Reservation r 
        SET r.status = :newStatus, r.updatedAt = CURRENT_TIMESTAMP
        WHERE r.id = :reservationId 
        AND r.status = :expectedStatus
    """)
    int updateStatusAtomically(
        @Param("reservationId") Long reservationId,
        @Param("expectedStatus") ReservationStatus expectedStatus,
        @Param("newStatus") ReservationStatus newStatus
    );

    @Query("""
        SELECT r FROM Reservation r 
        WHERE r.status = 'PENDING'
        AND r.expiresAt IS NOT NULL
        AND r.expiresAt < :now
        ORDER BY r.expiresAt ASC
    """)
    List<Reservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);
}
