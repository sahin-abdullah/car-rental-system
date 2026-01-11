package com.rental.car.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "cars")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType type; 

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @NotBlank
    private String make;
    @NotBlank
    private String model;
    @NotNull
    private int year;

    @ManyToOne(optional = false) 
    @JoinColumn(name = "current_branch_id", nullable = false)
    private Branch currentBranch; 

    private boolean available = true;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
