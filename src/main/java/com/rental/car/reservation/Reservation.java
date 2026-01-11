package com.rental.car.reservation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations", 
    indexes = {
        @Index(name = "idx_car_dates", columnList = "car_id,pickup_date,return_date"),
        @Index(name = "idx_customer_email", columnList = "customer_email"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_pickup_branch", columnList = "pickup_branch_code"),
        @Index(name = "idx_return_branch", columnList = "return_branch_code")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "car_id", nullable = false)
    private Long carId;

    @NotNull
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @NotNull
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @NotNull
    @Column(name = "pickup_branch_code", nullable = false, length = 10)
    private String pickupBranchCode;

    @NotNull
    @Column(name = "return_branch_code", nullable = false, length = 10)
    private String returnBranchCode;

    @NotNull
    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @NotNull
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @NotNull
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ReservationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public long getRentalDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(pickupDate, returnDate);
    }

    public boolean isExpired() {
        return status == ReservationStatus.PENDING 
            && expiresAt != null 
            && LocalDateTime.now().isAfter(expiresAt);
    }
}
