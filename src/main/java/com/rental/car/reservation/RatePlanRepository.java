package com.rental.car.reservation;

import com.rental.car.inventory.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    /**
     * Find active rate plan for specific branch, car type, and date
     */
    @Query("""
        SELECT rp FROM RatePlan rp 
        WHERE rp.branchCode = :branchCode 
        AND rp.carType = :carType 
        AND rp.active = true
        AND :date >= rp.effectiveFrom 
        AND :date <= rp.effectiveTo
        ORDER BY rp.effectiveFrom DESC
        LIMIT 1
    """)
    Optional<RatePlan> findApplicableRatePlan(
        @Param("branchCode") String branchCode,
        @Param("carType") CarType carType,
        @Param("date") LocalDate date
    );

    /**
     * Check if rate plan exists for branch and car type
     */
    boolean existsByBranchCodeAndCarTypeAndActiveTrue(String branchCode, CarType carType);
}
