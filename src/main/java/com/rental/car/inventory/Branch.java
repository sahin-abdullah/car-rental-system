package com.rental.car.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "branches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber; 

    @Column(nullable = false)
    private Boolean isAirport = false;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    // Convenience constructor without isAirport (defaults to false)
    public Branch(Long id, String code, String name, String phoneNumber, Address address) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isAirport = false;
        this.address = address;
    }
}
