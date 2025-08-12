package com.wego.carpark.repositories;

import com.wego.carpark.entities.CarPark;
import com.wego.carpark.entities.CarParkAvailability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CarParkAvailabilityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    private CarPark testCarPark;
    private CarParkAvailability testAvailability;

    @BeforeEach
    void setUp() {
        testCarPark = new CarPark();
        testCarPark.setId("TEST001");
        testCarPark.setAddress("Test Car Park Address");
        testCarPark.setLatitude(1.3521);
        testCarPark.setLongitude(103.8198);
        testCarPark.setTotalLots(100);

        testAvailability = new CarParkAvailability();
        testAvailability.setCarPark(testCarPark);
        testAvailability.setAvailableLots(10);
    }

    @Test
    @DisplayName("Should save car park availability successfully")
    void testSaveCarParkAvailability() {
        // Given
        entityManager.persistAndFlush(testCarPark);

        // When
        CarParkAvailability savedAvailability = carParkAvailabilityRepository.save(testAvailability);

        // Then
        assertNotNull(savedAvailability);
        assertEquals(testCarPark, savedAvailability.getCarPark());
        assertEquals(10, savedAvailability.getAvailableLots());
    }

    @Test
    @DisplayName("Should find car parks with available lots greater than zero")
    void testFindByAvailableLotsGreaterThan() {
        // Given
        entityManager.persistAndFlush(testCarPark);

        CarParkAvailability availability1 = new CarParkAvailability();
        availability1.setCarPark(testCarPark);
        availability1.setAvailableLots(5);

        CarPark carPark2 = new CarPark();
        carPark2.setId("TEST002");
        carPark2.setAddress("Test Car Park 2");
        carPark2.setLatitude(1.3522);
        carPark2.setLongitude(103.8199);
        carPark2.setTotalLots(50);
        entityManager.persistAndFlush(carPark2);

        CarParkAvailability availability2 = new CarParkAvailability();
        availability2.setCarPark(carPark2);
        availability2.setAvailableLots(0);

        CarPark carPark3 = new CarPark();
        carPark3.setId("TEST003");
        carPark3.setAddress("Test Car Park 3");
        carPark3.setLatitude(1.3523);
        carPark3.setLongitude(103.8200);
        carPark3.setTotalLots(75);
        entityManager.persistAndFlush(carPark3);

        CarParkAvailability availability3 = new CarParkAvailability();
        availability3.setCarPark(carPark3);
        availability3.setAvailableLots(15);

        entityManager.persistAndFlush(availability1);
        entityManager.persistAndFlush(availability2);
        entityManager.persistAndFlush(availability3);

        // When
        List<CarParkAvailability> availableCarParks = carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0);

        // Then
        assertEquals(2, availableCarParks.size());
        assertTrue(availableCarParks.stream().anyMatch(av -> av.getAvailableLots() == 5));
        assertTrue(availableCarParks.stream().anyMatch(av -> av.getAvailableLots() == 15));
        assertFalse(availableCarParks.stream().anyMatch(av -> av.getAvailableLots() == 0));
    }

    @Test
    @DisplayName("Should return empty list when no car parks have available lots")
    void testFindByAvailableLotsGreaterThan_NoAvailableLots() {
        // Given
        entityManager.persistAndFlush(testCarPark);

        CarParkAvailability availability1 = new CarParkAvailability();
        availability1.setCarPark(testCarPark);
        availability1.setAvailableLots(0);

        CarPark carPark2 = new CarPark();
        carPark2.setId("TEST002");
        carPark2.setAddress("Test Car Park 2");
        carPark2.setLatitude(1.3522);
        carPark2.setLongitude(103.8199);
        carPark2.setTotalLots(50);
        entityManager.persistAndFlush(carPark2);

        CarParkAvailability availability2 = new CarParkAvailability();
        availability2.setCarPark(carPark2);
        availability2.setAvailableLots(0);

        entityManager.persistAndFlush(availability1);
        entityManager.persistAndFlush(availability2);

        // When
        List<CarParkAvailability> availableCarParks = carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0);

        // Then
        assertTrue(availableCarParks.isEmpty());
    }

    @Test
    @DisplayName("Should find car parks with available lots greater than specified threshold")
    void testFindByAvailableLotsGreaterThan_WithThreshold() {
        // Given
        entityManager.persistAndFlush(testCarPark);

        CarParkAvailability availability1 = new CarParkAvailability();
        availability1.setCarPark(testCarPark);
        availability1.setAvailableLots(5);

        CarPark carPark2 = new CarPark();
        carPark2.setId("TEST002");
        carPark2.setAddress("Test Car Park 2");
        carPark2.setLatitude(1.3522);
        carPark2.setLongitude(103.8199);
        carPark2.setTotalLots(50);
        entityManager.persistAndFlush(carPark2);

        CarParkAvailability availability2 = new CarParkAvailability();
        availability2.setCarPark(carPark2);
        availability2.setAvailableLots(15);

        CarPark carPark3 = new CarPark();
        carPark3.setId("TEST003");
        carPark3.setAddress("Test Car Park 3");
        carPark3.setLatitude(1.3523);
        carPark3.setLongitude(103.8200);
        carPark3.setTotalLots(75);
        entityManager.persistAndFlush(carPark3);

        CarParkAvailability availability3 = new CarParkAvailability();
        availability3.setCarPark(carPark3);
        availability3.setAvailableLots(20);

        entityManager.persistAndFlush(availability1);
        entityManager.persistAndFlush(availability2);
        entityManager.persistAndFlush(availability3);

        // When - find car parks with more than 10 available lots
        List<CarParkAvailability> availableCarParks = carParkAvailabilityRepository.findByAvailableLotsGreaterThan(10);

        // Then
        assertEquals(2, availableCarParks.size());
        assertTrue(availableCarParks.stream().anyMatch(av -> av.getAvailableLots() == 15));
        assertTrue(availableCarParks.stream().anyMatch(av -> av.getAvailableLots() == 20));
        assertFalse(availableCarParks.stream().anyMatch(av -> av.getAvailableLots() == 5));
    }

    @Test
    @DisplayName("Should update car park availability")
    void testUpdateCarParkAvailability() {
        // Given
        entityManager.persistAndFlush(testCarPark);
        entityManager.persistAndFlush(testAvailability);

        // When
        testAvailability.setAvailableLots(25);
        CarParkAvailability updatedAvailability = carParkAvailabilityRepository.save(testAvailability);

        // Then
        assertEquals(25, updatedAvailability.getAvailableLots());
        
        // Verify the update is persisted
        List<CarParkAvailability> availableCarParks = carParkAvailabilityRepository.findByAvailableLotsGreaterThan(20);
        assertEquals(1, availableCarParks.size());
        assertEquals(25, availableCarParks.get(0).getAvailableLots());
    }

    @Test
    @DisplayName("Should handle negative available lots")
    void testNegativeAvailableLots() {
        // Given
        entityManager.persistAndFlush(testCarPark);

        CarParkAvailability availability = new CarParkAvailability();
        availability.setCarPark(testCarPark);
        availability.setAvailableLots(-5);

        // When
        CarParkAvailability savedAvailability = carParkAvailabilityRepository.save(availability);

        // Then
        assertEquals(-5, savedAvailability.getAvailableLots());
        
        // Should not be found when searching for positive available lots
        List<CarParkAvailability> availableCarParks = carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0);
        assertTrue(availableCarParks.isEmpty());
    }
}
