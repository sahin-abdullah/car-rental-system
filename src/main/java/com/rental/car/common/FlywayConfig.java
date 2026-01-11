package com.rental.car.common;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Runs Flyway migrations AFTER the Spring application context is fully initialized.
 * This ensures Hibernate has already created all tables before Flyway adds
 * foreign key constraints and seeds data.
 */
@Component
public class FlywayConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);
    private final DataSource dataSource;

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running Flyway migrations after application startup...");
        
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .load();
        
        // Baseline the existing schema (tables created by Hibernate)
        flyway.baseline();
        
        // Run pending migrations (foreign keys and data seeding)
        flyway.migrate();
        
        log.info("Flyway migrations completed successfully");
    }
}
