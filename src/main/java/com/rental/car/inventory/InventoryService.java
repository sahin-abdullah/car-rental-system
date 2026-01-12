package com.rental.car.inventory;

import com.rental.car.exceptions.DuplicateResourceException;
import com.rental.car.common.GeocodingService;
import com.rental.car.exceptions.ResourceNotFoundException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {

    private final CarRepository carRepo;
    private final BranchRepository branchRepo;
    private final GeocodingService geoService;

    public InventoryService(CarRepository carRepo, BranchRepository branchRepo, GeocodingService geoService) {
        this.carRepo = carRepo;
        this.branchRepo = branchRepo;
        this.geoService = geoService;
    }

    @Cacheable(value = "carSearch", key = "#address + '-' + #branchCode + '-' + #type + '-' + #make + '-' + #year + '-' + #pickupDate + '-' + #returnDate + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<CarDTO> searchWithFilters(
            String address,
            String branchCode,
            CarType type,
            String make,
            Integer year,
            LocalDate pickupDate,
            LocalDate returnDate,
            Pageable pageable
    ) {
        final Double searchLat;
        final Double searchLon;
        final Double searchRadius;
        
        if (address != null && !address.isBlank() && branchCode == null) {
            double[] coords = geoService.getCoordinates(address);
            if (coords[0] != 0.0 && coords[1] != 0.0) {
                searchLat = coords[0];
                searchLon = coords[1];
                searchRadius = MAX_DISTANCE_KM;
            } else {
                searchLat = null;
                searchLon = null;
                searchRadius = null;
            }
        } else {
            searchLat = null;
            searchLon = null;
            searchRadius = null;
        }

        return carRepo.searchFleetUnified(
                searchLat, searchLon, searchRadius,
                branchCode, 
                type != null ? type.name() : null,
                make, year, pickupDate, returnDate, pageable
        ).map(result -> new CarDTO(
            result.getId(),
            CarType.valueOf(result.getType()),
            result.getLicensePlate(),
            result.getMake(),
            result.getModel(),
            result.getYear(),
            result.getBranchCode(),
            result.getBranchName(),
            result.getBranchCity(),
            result.getAvailable(),
            result.getDistanceKm()
        ));
    }

    private static final double MAX_DISTANCE_KM = 100.0;

    @Transactional(readOnly = true)
    public List<BranchWithDistance> findNearestBranches(double userLat, double userLon, int limit) {
        return branchRepo.findNearestBranches(userLat, userLon, limit);
    }

    @Transactional(readOnly = true)
    public boolean isValidBranch(String branchCode) {
        return branchRepo.existsByCode(branchCode);
    }

    @Transactional(readOnly = true)
    public Page<Branch> getAllBranches(Pageable pageable) {
        return branchRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Branch> getBranchByCode(String code) {
        return branchRepo.findByCode(code);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<CarDTO> getCarById(Long id) {
        return carRepo.findById(id)
                .map(car -> new CarDTO(
                        car.getId(),
                        car.getType(),
                        car.getLicensePlate(),
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getCurrentBranch().getCode(),
                        car.getCurrentBranch().getName(),
                        car.getCurrentBranch().getAddress().getCity(),
                        car.isAvailable(),
                        null
                ));
    }

    @Transactional(readOnly = true)
    public List<CarType> getAvailableTypesAtBranch(String branchCode) {
        return carRepo.findAvailableTypes(branchCode);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAvailableMakesAtBranch(String branchCode, CarType type) {
        return carRepo.findAvailableMakes(branchCode, type);
    }
    
    @Transactional(readOnly = true)
    public List<Integer> getAvailableYearsAtBranch(String branchCode, CarType type, String make) {
        return carRepo.findAvailableYears(branchCode, type, make);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public void updateCarAvailability(Long carId, boolean available) {
        Car car = carRepo.findById(carId)
                .orElseThrow(() -> ResourceNotFoundException.car(carId));
        
        if (car.isAvailable() == available) {
            return;  
        }
        
        car.setAvailable(available);
        carRepo.save(car);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public void moveCarToBranch(Long carId, String branchCode) {
        Car car = carRepo.findById(carId)
                .orElseThrow(() -> ResourceNotFoundException.car(carId));
        Branch targetBranch = branchRepo.findByCode(branchCode)
                .orElseThrow(() -> ResourceNotFoundException.branch(branchCode));
        
        if (car.getCurrentBranch().getId().equals(targetBranch.getId())) {
            return;
        }
        
        car.setCurrentBranch(targetBranch);
        carRepo.save(car);  // @Version will detect if car was modified concurrently
    }


    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Car createCar(CarType type, String plate, String make, String model, int year, String branchCode, boolean available) {
        if (carRepo.existsByLicensePlate(plate)) {
            throw DuplicateResourceException.carLicensePlate(plate);
        }
        Branch branch = branchRepo.findByCode(branchCode)
                .orElseThrow(() -> ResourceNotFoundException.branch(branchCode));
        
        Car car = new Car(null, type, plate, make, model, year, branch, available, null);  // version managed by JPA
        return carRepo.save(car);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Car updateCar(Long carId, CarType type, String plate, String make, String model, int year, String branchCode, Boolean available) {
        Car car = carRepo.findById(carId)
                .orElseThrow(() -> ResourceNotFoundException.car(carId));
        
        if (!car.getLicensePlate().equals(plate) && carRepo.existsByLicensePlate(plate)) {
            throw DuplicateResourceException.carLicensePlate(plate);
        }
        
        Branch branch = branchRepo.findByCode(branchCode)
                .orElseThrow(() -> ResourceNotFoundException.branch(branchCode));
        
        car.setType(type);
        car.setLicensePlate(plate);
        car.setMake(make);
        car.setModel(model);
        car.setYear(year);
        car.setCurrentBranch(branch);
        if (available != null) {
            car.setAvailable(available);
        }
        
        return carRepo.save(car);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public void deleteCar(Long carId) {
        if (!carRepo.existsById(carId)) {
            throw ResourceNotFoundException.car(carId);
        }
        carRepo.deleteById(carId);
    }

    @Transactional
    public Branch createBranchWithDetails(String code, String name, String phoneNumber, 
                                          String street1, String city, String state, 
                                          String country, String zipCode) {
        if (branchRepo.existsByCode(code)) {
            throw DuplicateResourceException.branchCode(code);
        }
        
        String query = street1 + ", " + city + ", " + state + ", " + country;
        double[] coords = geoService.getCoordinates(query);
        
        Address address = new Address(null, street1, null, city, state, zipCode, country, coords[0], coords[1]);
        Branch branch = new Branch(null, code, name, phoneNumber, address);
        
        return branchRepo.save(branch);
    }

    @Transactional
    public Branch updateBranch(String code, String name, String phoneNumber, 
                               String street1, String city, String state, 
                               String country, String zipCode) {
        Branch branch = branchRepo.findByCode(code)
                .orElseThrow(() -> ResourceNotFoundException.branch(code));
        
        branch.setName(name);
        branch.setPhoneNumber(phoneNumber);
        
        Address address = branch.getAddress();
        boolean addressChanged = !address.getStreet1().equals(street1) || 
                                !address.getCity().equals(city) || 
                                !address.getState().equals(state) || 
                                !address.getCountry().equals(country);
        
        if (addressChanged) {
            String query = street1 + ", " + city + ", " + state + ", " + country;
            double[] coords = geoService.getCoordinates(query);
            address.setLatitude(coords[0]);
            address.setLongitude(coords[1]);
        }
        
        address.setStreet1(street1);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setZipCode(zipCode);
        
        return branchRepo.save(branch);
    }

    @Transactional
    public void deleteBranch(String code) {
        Branch branch = branchRepo.findByCode(code)
                .orElseThrow(() -> ResourceNotFoundException.branch(code));
        branchRepo.delete(branch);
    }

}
