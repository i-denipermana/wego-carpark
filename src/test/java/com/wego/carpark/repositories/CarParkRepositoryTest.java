package com.wego.carpark.repositories;

import com.wego.carpark.entities.CarPark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CarParkRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarParkRepository carParkRepository;

    private CarPark testCarPark;

    @BeforeEach
    void setUp() {
        testCarPark = new CarPark();
        testCarPark.setId("TEST001");
        testCarPark.setAddress("Test Car Park Address");
        testCarPark.setLatitude(1.3521);
        testCarPark.setLongitude(103.8198);
        testCarPark.setTotalLots(100);
        testCarPark.setSvy21X(12345.67);
        testCarPark.setSvy21Y(23456.78);
    }

    @Test
    @DisplayName("Should save car park successfully")
    void testSaveCarPark() {
        // When
        CarPark savedCarPark = carParkRepository.save(testCarPark);

        // Then
        assertNotNull(savedCarPark);
        assertEquals("TEST001", savedCarPark.getId());
        assertEquals("Test Car Park Address", savedCarPark.getAddress());
        assertEquals(1.3521, savedCarPark.getLatitude());
        assertEquals(103.8198, savedCarPark.getLongitude());
        assertEquals(100, savedCarPark.getTotalLots());
    }

    @Test
    @DisplayName("Should find car park by ID")
    void testFindById() {
        // Given
        entityManager.persistAndFlush(testCarPark);

        // When
        Optional<CarPark> foundCarPark = carParkRepository.findById("TEST001");

        // Then
        assertTrue(foundCarPark.isPresent());
        assertEquals("TEST001", foundCarPark.get().getId());
        assertEquals("Test Car Park Address", foundCarPark.get().getAddress());
    }

    @Test
    @DisplayName("Should return empty when car park not found")
    void testFindById_NotFound() {
        // When
        Optional<CarPark> foundCarPark = carParkRepository.findById("NONEXISTENT");

        // Then
        assertFalse(foundCarPark.isPresent());
    }

    @Test
    @DisplayName("Should update existing car park")
    void testUpdateCarPark() {
        // Given
        entityManager.persistAndFlush(testCarPark);
        
        CarPark updatedCarPark = new CarPark();
        updatedCarPark.setId("TEST001");
        updatedCarPark.setAddress("Updated Car Park Address");
        updatedCarPark.setLatitude(1.3522);
        updatedCarPark.setLongitude(103.8199);
        updatedCarPark.setTotalLots(150);

        // When
        CarPark savedUpdatedCarPark = carParkRepository.save(updatedCarPark);

        // Then
        assertEquals("TEST001", savedUpdatedCarPark.getId());
        assertEquals("Updated Car Park Address", savedUpdatedCarPark.getAddress());
        assertEquals(1.3522, savedUpdatedCarPark.getLatitude());
        assertEquals(103.8199, savedUpdatedCarPark.getLongitude());
        assertEquals(150, savedUpdatedCarPark.getTotalLots());
    }

    @Test
    @DisplayName("Should save multiple car parks")
    void testSaveMultipleCarParks() {
        // Given
        CarPark carPark1 = new CarPark();
        carPark1.setId("TEST001");
        carPark1.setAddress("Car Park 1");
        carPark1.setLatitude(1.3521);
        carPark1.setLongitude(103.8198);
        carPark1.setTotalLots(100);

        CarPark carPark2 = new CarPark();
        carPark2.setId("TEST002");
        carPark2.setAddress("Car Park 2");
        carPark2.setLatitude(1.3522);
        carPark2.setLongitude(103.8199);
        carPark2.setTotalLots(50);

        // When
        carParkRepository.saveAll(Arrays.asList(carPark1, carPark2));

        // Then
        Optional<CarPark> found1 = carParkRepository.findById("TEST001");
        Optional<CarPark> found2 = carParkRepository.findById("TEST002");
        
        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals("Car Park 1", found1.get().getAddress());
        assertEquals("Car Park 2", found2.get().getAddress());
    }

    @Test
    @DisplayName("Should handle car park with all fields")
    void testCarParkWithAllFields() {
        // Given
        CarPark fullCarPark = new CarPark();
        fullCarPark.setId("FULL001");
        fullCarPark.setAddress("Full Car Park");
        fullCarPark.setLatitude(1.3521);
        fullCarPark.setLongitude(103.8198);
        fullCarPark.setTotalLots(200);
        fullCarPark.setSvy21X(12345.67);
        fullCarPark.setSvy21Y(23456.78);

        // When
        CarPark savedCarPark = carParkRepository.save(fullCarPark);

        // Then
        assertEquals("FULL001", savedCarPark.getId());
        assertEquals("Full Car Park", savedCarPark.getAddress());
        assertEquals(1.3521, savedCarPark.getLatitude());
        assertEquals(103.8198, savedCarPark.getLongitude());
        assertEquals(200, savedCarPark.getTotalLots());
        assertEquals(12345.67, savedCarPark.getSvy21X());
        assertEquals(23456.78, savedCarPark.getSvy21Y());
    }
}
