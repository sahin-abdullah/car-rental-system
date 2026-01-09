package com.rental.car.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

interface BranchRepository extends JpaRepository<Branch, Long> {
    
    Optional<Branch> findByCode(String code);
    
    boolean existsByCode(String code);

    List<Branch> findByAddressCityIgnoreCase(String city);

    @Query(value = """
        SELECT 
            b.id AS id,
            b.code AS code,
            b.name AS name,
            b.phone_number AS phoneNumber,
            a.street1 AS street1,
            a.city AS city,
            a.state AS state,
            a.country AS country,
            a.zip_code AS zipCode,
            a.latitude AS latitude,
            a.longitude AS longitude,
            ST_Distance(
                geography(ST_MakePoint(:lon, :lat)),
                geography(ST_MakePoint(a.longitude, a.latitude))
            ) / 1000.0 AS distanceKm
        FROM branches b
        JOIN addresses a ON b.address_id = a.id
        ORDER BY distanceKm ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<BranchWithDistance> findNearestBranches(
        @Param("lat") double lat,
        @Param("lon") double lon,
        @Param("limit") int limit
    );
}
