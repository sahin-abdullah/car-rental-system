package com.rental.car.reservation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, length = 50)
    private String ruleCode;

    @NotNull
    @Column(nullable = false, length = 200)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false, length = 30)
    private PricingRuleType ruleType;

    // For percentage-based rules (e.g., tax rate, discount)
    @Column(name = "percentage_value", precision = 5, scale = 2)
    private BigDecimal percentageValue;

    // For fixed amount rules (e.g., one-way fee, airport surcharge)
    @Column(name = "fixed_amount", precision = 10, scale = 2)
    private BigDecimal fixedAmount;

    // Min rental days for discount to apply
    @Column(name = "min_days")
    private Integer minDays;

    @Column(name = "active")
    private boolean active = true;

    public enum PricingRuleType {
        TAX,                   
        ONE_WAY_FEE,           
        LENGTH_DISCOUNT,        
        WEEKEND_SURCHARGE,     
        AIRPORT_FEE,          
        INSURANCE_RATE,       
        ADDITIONAL_DRIVER_FEE 
    }
}
