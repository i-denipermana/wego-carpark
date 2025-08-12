package com.wego.carpark.services;

import com.wego.carpark.dto.responses.CarParkResponse;
import com.wego.carpark.entities.CarPark;
import com.wego.carpark.entities.CarParkAvailability;
import com.wego.carpark.repositories.CarParkAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CarParkServiceTest {

    @Mock
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    private CarParkService carParkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carParkService = new CarParkService(carParkAvailabilityRepository);
    }

    @Test
    @DisplayName("Should return empty list when no car parks are available")
    void testFindNearestCarParks_NoAvailableCarParks() {
        // Given
        when(carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0))
                .thenReturn(Collections.emptyList());

        // When
        List<CarParkResponse> result = carParkService.findNearestCarParks(1.0, 1.0, 1, 10);

        // Then
        assertTrue(result.isEmpty());
        verify(carParkAvailabilityRepository).findByAvailableLotsGreaterThan(0);
    }

    @Test
    @DisplayName("Should return car parks sorted by distance")
    void testFindNearestCarParks_SortedByDistance() {
        // Given
        CarPark carPark1 = createCarPark("Car Park 1", 1.0, 1.0, 100);
        CarPark carPark2 = createCarPark("Car Park 2", 2.0, 2.0, 50);
        CarPark carPark3 = createCarPark("Car Park 3", 0.5, 0.5, 75);

        CarParkAvailability availability1 = createAvailability(carPark1, 10);
        CarParkAvailability availability2 = createAvailability(carPark2, 5);
        CarParkAvailability availability3 = createAvailability(carPark3, 15);

        List<CarParkAvailability> availabilities = Arrays.asList(availability1, availability2, availability3);

        when(carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0))
                .thenReturn(availabilities);

        // When
        List<CarParkResponse> result = carParkService.findNearestCarParks(0.0, 0.0, 1, 10);

        // Then
        assertEquals(3, result.size());
        // Car park 3 should be closest to (0,0)
        assertEquals("Car Park 3", result.get(0).getAddress());
        // Car park 1 should be second closest
        assertEquals("Car Park 1", result.get(1).getAddress());
        // Car park 2 should be farthest
        assertEquals("Car Park 2", result.get(2).getAddress());
    }

    @Test
    @DisplayName("Should apply pagination correctly")
    void testFindNearestCarParks_WithPagination() {
        // Given
        CarPark carPark1 = createCarPark("Car Park 1", 1.0, 1.0, 100);
        CarPark carPark2 = createCarPark("Car Park 2", 2.0, 2.0, 50);
        CarPark carPark3 = createCarPark("Car Park 3", 3.0, 3.0, 75);

        CarParkAvailability availability1 = createAvailability(carPark1, 10);
        CarParkAvailability availability2 = createAvailability(carPark2, 5);
        CarParkAvailability availability3 = createAvailability(carPark3, 15);

        List<CarParkAvailability> availabilities = Arrays.asList(availability1, availability2, availability3);

        when(carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0))
                .thenReturn(availabilities);

        // When - page 2, per page 1
        List<CarParkResponse> result = carParkService.findNearestCarParks(0.0, 0.0, 2, 1);

        // Then
        assertEquals(1, result.size());
        assertEquals("Car Park 2", result.get(0).getAddress());
    }

    @Test
    @DisplayName("Should return empty list when page is out of bounds")
    void testFindNearestCarParks_PageOutOfBounds() {
        // Given
        CarPark carPark1 = createCarPark("Car Park 1", 1.0, 1.0, 100);
        CarParkAvailability availability1 = createAvailability(carPark1, 10);

        when(carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0))
                .thenReturn(List.of(availability1));

        // When - page 3, per page 1 (only 1 item available)
        List<CarParkResponse> result = carParkService.findNearestCarParks(0.0, 0.0, 3, 1);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should calculate distance correctly for each car park")
    void testFindNearestCarParks_DistanceCalculation() {
        // Given
        CarPark carPark = createCarPark("Test Car Park", 1.0, 1.0, 100);
        CarParkAvailability availability = createAvailability(carPark, 10);

        when(carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0))
                .thenReturn(List.of(availability));

        // When
        List<CarParkResponse> result = carParkService.findNearestCarParks(0.0, 0.0, 1, 10);

        // Then
        assertEquals(1, result.size());
        CarParkResponse response = result.get(0);
        assertEquals("Test Car Park", response.getAddress());
        assertEquals(1.0, response.getLatitude());
        assertEquals(1.0, response.getLongitude());
        assertEquals(100, response.getTotalLots());
        assertEquals(10, response.getAvailableLots());
        assertTrue(response.getDistance() > 0);
    }

    private CarPark createCarPark(String address, double latitude, double longitude, int totalLots) {
        CarPark carPark = new CarPark();
        carPark.setAddress(address);
        carPark.setLatitude(latitude);
        carPark.setLongitude(longitude);
        carPark.setTotalLots(totalLots);
        return carPark;
    }

    private CarParkAvailability createAvailability(CarPark carPark, int availableLots) {
        CarParkAvailability availability = new CarParkAvailability();
        availability.setCarPark(carPark);
        availability.setAvailableLots(availableLots);
        return availability;
    }
}
