package com.rental.car.inventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

public record CarDTO(
    Long id,
    CarType type,
    String licensePlate,
    String make,
    String model,
    int year,
    String branchCode,
    String branchName,
    String branchCity,
    boolean available,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Double distanceKm
) implements Serializable {
    static CarDTO from(Car car) {
        Branch branch = car.getCurrentBranch();
        return new CarDTO(
            car.getId(),
            car.getType(),
            car.getLicensePlate(),
            car.getMake(),
            car.getModel(),
            car.getYear(),
            branch.getCode(),
            branch.getName(),
            branch.getAddress().getCity(),
            car.isAvailable(),
            null
        );
    }
}
