package com.rental.car.inventory;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class CarTypeTest {

    @Test
    public void testEnumValues() {
        CarType[] types = CarType.values();
        
        assertEquals(types.length, 3);
        assertTrue(contains(types, CarType.SEDAN));
        assertTrue(contains(types, CarType.SUV));
        assertTrue(contains(types, CarType.VAN));
    }

    @Test
    public void testValueOf() {
        assertEquals(CarType.valueOf("SEDAN"), CarType.SEDAN);
        assertEquals(CarType.valueOf("SUV"), CarType.SUV);
        assertEquals(CarType.valueOf("VAN"), CarType.VAN);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValueOfInvalid() {
        CarType.valueOf("TRUCK");
    }

    @Test
    public void testEnumEquality() {
        CarType type1 = CarType.SEDAN;
        CarType type2 = CarType.SEDAN;
        
        assertEquals(type1, type2);
        assertSame(type1, type2);
    }

    @Test
    public void testEnumInequality() {
        CarType sedan = CarType.SEDAN;
        CarType suv = CarType.SUV;
        
        assertNotEquals(sedan, suv);
    }

    @Test
    public void testEnumName() {
        assertEquals(CarType.SEDAN.name(), "SEDAN");
        assertEquals(CarType.SUV.name(), "SUV");
        assertEquals(CarType.VAN.name(), "VAN");
    }

    @Test
    public void testEnumOrdinal() {
        assertEquals(CarType.SEDAN.ordinal(), 0);
        assertEquals(CarType.SUV.ordinal(), 1);
        assertEquals(CarType.VAN.ordinal(), 2);
    }

    private boolean contains(CarType[] types, CarType type) {
        for (CarType t : types) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }
}
