package com.rental.car.inventory;

interface CarWithDistance {
    Long getId();
    String getType();
    String getLicensePlate();
    String getMake();
    String getModel();
    Integer getYear();
    String getBranchCode();
    String getBranchName();
    String getBranchCity();
    Boolean getAvailable();
    Double getDistanceKm();
}
