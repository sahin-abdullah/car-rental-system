package com.rental.car.inventory;

import com.rental.car.common.DuplicateResourceException;
import com.rental.car.common.GeocodingService;
import com.rental.car.common.ResourceNotFoundException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Cacheable(value = "carSearch", key = "#address + '-' + #branchCode + '-' + #type + '-' + #make + '-' + #year + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<CarDTO> searchWithFilters(
            String address,
            String branchCode,
            CarType type,
            String make,
            Integer year,
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
                make, year, pageable
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

    @Transactional
    public void createBranch(String code, String name, String phone, Address address) {
        if (!branchRepo.existsByCode(code)) {
            String query = address.getStreet1() + ", " + address.getCity() + ", " + 
                          address.getState() + ", " + address.getCountry();

            double[] coords = geoService.getCoordinates(query);
            address.setLatitude(coords[0]);
            address.setLongitude(coords[1]);

            Branch branch = new Branch(null, code, name, phone, address);
            branchRepo.save(branch);
            System.out.println("Created Branch: " + name + " [" + coords[0] + ", " + coords[1] + "]");
        }
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public void addCar(CarType type, String plate, String make, String model, int year, String branchCode) {
        if (!carRepo.existsByLicensePlate(plate)) {
            Branch branch = branchRepo.findByCode(branchCode)
                    .orElseThrow(() -> ResourceNotFoundException.branch(branchCode));
            
            Car car = new Car(null, type, plate, make, model, year, branch, true);
            carRepo.save(car);
        }
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
    public java.util.Optional<Car> getCarById(Long id) {
        return carRepo.findById(id);
    }

    // Branch-centric filtering methods
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
        car.setCurrentBranch(targetBranch);
        carRepo.save(car);
    }


    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Car createCar(CarType type, String plate, String make, String model, int year, String branchCode, boolean available) {
        if (carRepo.existsByLicensePlate(plate)) {
            throw DuplicateResourceException.carLicensePlate(plate);
        }
        Branch branch = branchRepo.findByCode(branchCode)
                .orElseThrow(() -> ResourceNotFoundException.branch(branchCode));
        
        Car car = new Car(null, type, plate, make, model, year, branch, available);
        return carRepo.save(car);
    }

    @CacheEvict(value = "carSearch", allEntries = true)
    @Transactional
    public Car updateCar(Long carId, CarType type, String plate, String make, String model, int year, String branchCode, boolean available) {
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
        car.setAvailable(available);
        
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
