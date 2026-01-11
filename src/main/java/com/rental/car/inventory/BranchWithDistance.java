package com.rental.car.inventory;

interface BranchWithDistance {
    Long getId();
    String getCode();
    String getName();
    String getPhoneNumber();
    String getStreet1();
    String getCity();
    String getState();
    String getCountry();
    String getZipCode();
    Double getLatitude();
    Double getLongitude();
    Double getDistanceKm();
}
