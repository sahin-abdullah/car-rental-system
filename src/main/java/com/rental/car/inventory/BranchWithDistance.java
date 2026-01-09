package com.rental.car.inventory;

interface BranchWithDistance {
    Long getId();
    String getCode();
    String getName();
    String getPhone();
    String getStreet1();
    String getCity();
    String getState();
    String getCountry();
    String getPostalCode();
    Double getLatitude();
    Double getLongitude();
    Double getDistanceKm();
}
