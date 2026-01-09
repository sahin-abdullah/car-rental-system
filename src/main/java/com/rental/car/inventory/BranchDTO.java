package com.rental.car.inventory;

public record BranchDTO(
    Long id,
    String code,
    String name,
    String phoneNumber,
    String street,
    String city,
    String state,
    String country,
    String zipCode,
    Double latitude,
    Double longitude
) {
    static BranchDTO from(Branch branch) {
        Address addr = branch.getAddress();
        return new BranchDTO(
            branch.getId(),
            branch.getCode(),
            branch.getName(),
            branch.getPhoneNumber(),
            addr.getStreet1(),
            addr.getCity(),
            addr.getState(),
            addr.getCountry(),
            addr.getZipCode(),
            addr.getLatitude(),
            addr.getLongitude()
        );
    }
}
