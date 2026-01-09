package com.rental.car.inventory;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class DatabaseMigration {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void createSpatialIndexes() {
        try {
            jdbcTemplate.execute(
                "CREATE INDEX IF NOT EXISTS idx_address_location " +
                "ON addresses USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326))"
            );
            System.out.println("✓ Spatial index (geometry) created on addresses table");
            
            jdbcTemplate.execute(
                "CREATE INDEX IF NOT EXISTS idx_addresses_geog " +
                "ON addresses USING GIST (geography(ST_MakePoint(longitude, latitude)))"
            );
            System.out.println("✓ Spatial index (geography) created on addresses table");
        } catch (Exception e) {
            System.err.println("Warning: Could not create spatial indexes: " + e.getMessage());
        }
    }
}
