package com.rental.car.reservation;

import com.rental.car.inventory.CarType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rate_plans", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "car_type", "effective_from"}),
    indexes = {
        @Index(name = "idx_rate_plan_lookup", columnList = "branch_code,car_type,effective_from,effective_to"),
        @Index(name = "idx_rate_plan_branch", columnList = "branch_code")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class RatePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "branch_code", nullable = false, length = 10)
    private String branchCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false, length = 20)
    private CarType carType;

    @NotNull
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @NotNull
    @Column(name = "effective_to", nullable = false)
    private LocalDate effectiveTo;

    @NotNull
    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @NotNull
    @Column(name = "weekly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal weeklyRate;

    @Column(name = "weekend_multiplier", precision = 4, scale = 2)
    private BigDecimal weekendMultiplier; // e.g., 1.2 = 20% increase on weekends

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "active")
    private boolean active = true;

    public boolean isValidForDate(LocalDate date) {
        return active && 
               !date.isBefore(effectiveFrom) && 
               !date.isAfter(effectiveTo);
    }
}
