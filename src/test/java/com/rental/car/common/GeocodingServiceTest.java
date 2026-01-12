package com.rental.car.common;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class GeocodingServiceTest {

    private GeocodingService geocodingService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        geocodingService = new GeocodingService();
    }

    @Test
    public void testGetCoordinatesReturnsArray() {
        double[] coordinates = geocodingService.getCoordinates("Los Angeles, CA");

        assertNotNull(coordinates);
        assertEquals(coordinates.length, 2);
    }

    @Test
    public void testGetCoordinatesWithEmptyAddress() {
        double[] coordinates = geocodingService.getCoordinates("");

        assertNotNull(coordinates);
        assertEquals(coordinates.length, 2);
        // Should return default [0.0, 0.0] on failure
        assertEquals(coordinates[0], 0.0);
        assertEquals(coordinates[1], 0.0);
    }

    @Test
    public void testGetCoordinatesWithInvalidAddress() {
        double[] coordinates = geocodingService.getCoordinates("INVALID_ADDRESS_THAT_DOES_NOT_EXIST_12345");

        assertNotNull(coordinates);
        assertEquals(coordinates.length, 2);
        // Should return default [0.0, 0.0] on failure or no results
        assertEquals(coordinates[0], 0.0);
        assertEquals(coordinates[1], 0.0);
    }

    @Test
    public void testGetCoordinatesWithNullAddress() {
        double[] coordinates = geocodingService.getCoordinates(null);

        assertNotNull(coordinates);
        assertEquals(coordinates.length, 2);
        // Should handle null gracefully and return default [0.0, 0.0]
        assertEquals(coordinates[0], 0.0);
        assertEquals(coordinates[1], 0.0);
    }

    @Test
    public void testCoordinatesArrayStructure() {
        double[] coordinates = geocodingService.getCoordinates("Test Address");

        assertNotNull(coordinates);
        assertEquals(coordinates.length, 2);
        // First element is latitude, second is longitude
        assertTrue(coordinates[0] >= -90.0 && coordinates[0] <= 90.0);
        assertTrue(coordinates[1] >= -180.0 && coordinates[1] <= 180.0);
    }

    @Test
    public void testMultipleCallsWithSameAddress() {
        String address = "Test Location";
        
        double[] coordinates1 = geocodingService.getCoordinates(address);
        double[] coordinates2 = geocodingService.getCoordinates(address);

        assertNotNull(coordinates1);
        assertNotNull(coordinates2);
        // Should return same results (cached)
        assertEquals(coordinates1[0], coordinates2[0]);
        assertEquals(coordinates1[1], coordinates2[1]);
    }
}
