package com.rental.car.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InventorySeeder implements CommandLineRunner {

    private final InventoryService service;

    public InventorySeeder(InventoryService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        System.out.println("🌱 Seeding Inventory...");
        
        Address addrJfk = new Address(
            null, "JFK Airport", null, "Queens", "NY", "11430", "USA", 0.0, 0.0
        );
        service.createBranch("JFK", "JFK International", "555-0100", addrJfk);

        Address addrLax = new Address(
            null, "Los Angeles International Airport", null, "Los Angeles", "CA", "90045", "USA", 0.0, 0.0
        );
        service.createBranch("LAX", "Los Angeles Intl", "555-0200", addrLax);

        Address addrNyc = new Address(
            null, "350 W 34th St", null, "New York", "NY", "10001", "USA", 0.0, 0.0
        );
        service.createBranch("NYC-PENN", "Manhattan Penn Station", "555-0300", addrNyc);

        // Cars
        service.addCar(CarType.SEDAN, "NY-SED-01", "Toyota", "Camry", 2024, "JFK");
        service.addCar(CarType.SEDAN, "NY-SED-02", "Honda", "Accord", 2023, "JFK");
        service.addCar(CarType.SUV,   "NY-SUV-01", "Jeep", "Grand Cherokee", 2024, "NYC-PENN");
        service.addCar(CarType.SEDAN, "NY-SED-03", "Tesla", "Model 3", 2024, "NYC-PENN");
        service.addCar(CarType.SUV,   "CA-SUV-01", "Ford", "Explorer", 2024, "LAX");
        service.addCar(CarType.SEDAN, "CA-SED-01", "BMW", "3 Series", 2023, "LAX");

        System.out.println("✅ Inventory Seeded.");
    }
}
