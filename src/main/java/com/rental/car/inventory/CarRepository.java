package com.rental.car.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.type = :type AND c.currentBranch.code = :branchCode AND c.available = true")
    List<Car> findAvailableCarsAtBranch(@Param("type") CarType type, @Param("branchCode") String branchCode);

    Optional<Car> findByLicensePlate(String licensePlate);
    @Query("""
        SELECT c FROM Car c 
        WHERE c.available = true 
        AND (:branchCode IS NULL OR c.currentBranch.code = :branchCode)
        AND (:type IS NULL OR c.type = :type)
        AND (CAST(:make AS string) IS NULL OR LOWER(c.make) LIKE LOWER(CONCAT('%', CAST(:make AS string), '%')))
        AND (:year IS NULL OR c.year = :year)
    """)
    Page<Car> searchFleet(
        @Param("branchCode") String branchCode, 
        @Param("type") CarType type,            
        @Param("make") String make,             
        @Param("year") Integer year,            
        Pageable pageable
    );

    @Query(value = """
        SELECT 
            c.id AS id,
            c.type AS type,
            c.license_plate AS licensePlate,
            c.make AS make,
            c.model AS model,
            c.year AS year,
            b.code AS branchCode,
            b.name AS branchName,
            a.city AS branchCity,
            c.available AS available,
            CASE 
                WHEN :lat IS NULL OR :lon IS NULL THEN NULL
                ELSE ST_Distance(
                    geography(ST_MakePoint(:lon, :lat)),
                    geography(ST_MakePoint(a.longitude, a.latitude))
                ) / 1000.0
            END AS distanceKm
        FROM cars c
        JOIN branches b ON c.current_branch_id = b.id
        JOIN addresses a ON b.address_id = a.id
        WHERE c.available = true
        AND (:branchCode IS NULL OR b.code = :branchCode)
        AND (:type IS NULL OR c.type = :type)
        AND (:make IS NULL OR LOWER(c.make) LIKE LOWER(CONCAT('%', :make, '%')))
        AND (:year IS NULL OR c.year = :year)
        AND (
            :lat IS NULL OR :lon IS NULL OR :maxDistanceKm IS NULL
            OR ST_DWithin(
                geography(ST_MakePoint(:lon, :lat)),
                geography(ST_MakePoint(a.longitude, a.latitude)),
                :maxDistanceKm * 1000.0
            )
        )
        ORDER BY
            CASE WHEN :lat IS NULL OR :lon IS NULL THEN 1 ELSE 0 END,
            CASE WHEN :lat IS NULL OR :lon IS NULL THEN c.id ELSE NULL END ASC,
            distanceKm ASC
    """, 
    countQuery = """
        SELECT count(*)
        FROM cars c
        JOIN branches b ON c.current_branch_id = b.id
        JOIN addresses a ON b.address_id = a.id
        WHERE c.available = true
        AND (:branchCode IS NULL OR b.code = :branchCode)
        AND (:type IS NULL OR c.type = :type)
        AND (:make IS NULL OR LOWER(c.make) LIKE LOWER(CONCAT('%', :make, '%')))
        AND (:year IS NULL OR c.year = :year)
        AND (
            :lat IS NULL OR :lon IS NULL OR :maxDistanceKm IS NULL
            OR ST_DWithin(
                geography(ST_MakePoint(:lon, :lat)),
                geography(ST_MakePoint(a.longitude, a.latitude)),
                :maxDistanceKm * 1000.0
            )
        )
    """,
    nativeQuery = true)
    Page<CarWithDistance> searchFleetUnified(
        @Param("lat") Double lat,
        @Param("lon") Double lon,
        @Param("maxDistanceKm") Double maxDistanceKm,
        @Param("branchCode") String branchCode,
        @Param("type") String type,
        @Param("make") String make,
        @Param("year") Integer year,
        Pageable pageable
    );

    boolean existsByLicensePlate(String licensePlate);
    
    // Branch-centric filtering queries - branchCode is required
    @Query("""
        SELECT DISTINCT c.type FROM Car c 
        WHERE c.available = true 
        AND c.currentBranch.code = :branchCode
        ORDER BY c.type
    """)
    List<CarType> findAvailableTypes(@Param("branchCode") String branchCode);
    
    @Query("""
        SELECT DISTINCT c.make FROM Car c 
        WHERE c.available = true 
        AND c.currentBranch.code = :branchCode
        AND (:type IS NULL OR c.type = :type)
        ORDER BY c.make
    """)
    List<String> findAvailableMakes(@Param("branchCode") String branchCode, @Param("type") CarType type);
    
    @Query("""
        SELECT DISTINCT c.year FROM Car c 
        WHERE c.available = true 
        AND c.currentBranch.code = :branchCode
        AND (:type IS NULL OR c.type = :type)
        AND (CAST(:make AS string) IS NULL OR c.make = :make)
        ORDER BY c.year DESC
    """)
    List<Integer> findAvailableYears(@Param("branchCode") String branchCode, @Param("type") CarType type, @Param("make") String make);
}