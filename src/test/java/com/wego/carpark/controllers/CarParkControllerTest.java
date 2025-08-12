package com.wego.carpark.controllers;

import com.wego.carpark.dto.requests.NearestCarParksRequest;
import com.wego.carpark.dto.responses.CarParkResponse;
import com.wego.carpark.services.CarParkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarParkControllerTest {

    @Mock
    private CarParkService carParkService;

    private CarParkController carParkController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carParkController = new CarParkController(carParkService);
    }

    @Test
    @DisplayName("Should return car parks when valid request is provided")
    void testGetNearestCarParks_Success() {
        // Given
        NearestCarParksRequest request = new NearestCarParksRequest();
        request.setLatitude(1.3521);
        request.setLongitude(103.8198);
        request.setPage(1);
        request.setPerPage(10);

        CarParkResponse carPark1 = CarParkResponse.builder()
                .address("Car Park 1")
                .latitude(1.3521)
                .longitude(103.8198)
                .totalLots(100)
                .availableLots(10)
                .distance(0.5)
                .build();

        CarParkResponse carPark2 = CarParkResponse.builder()
                .address("Car Park 2")
                .latitude(1.3522)
                .longitude(103.8199)
                .totalLots(50)
                .availableLots(5)
                .distance(1.2)
                .build();

        List<CarParkResponse> expectedResponse = Arrays.asList(carPark1, carPark2);

        when(carParkService.findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<?> response = carParkController.getNearestCarParks(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(carParkService).findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        );
    }

    @Test
    @DisplayName("Should return empty list when no car parks found")
    void testGetNearestCarParks_EmptyResult() {
        // Given
        NearestCarParksRequest request = new NearestCarParksRequest();
        request.setLatitude(1.3521);
        request.setLongitude(103.8198);
        request.setPage(1);
        request.setPerPage(10);

        when(carParkService.findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        )).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<?> response = carParkController.getNearestCarParks(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(carParkService).findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        );
    }

    @Test
    @DisplayName("Should handle pagination parameters correctly")
    void testGetNearestCarParks_WithPagination() {
        // Given
        NearestCarParksRequest request = new NearestCarParksRequest();
        request.setLatitude(1.3521);
        request.setLongitude(103.8198);
        request.setPage(2);
        request.setPerPage(5);

        CarParkResponse carPark = CarParkResponse.builder()
                .address("Car Park Page 2")
                .latitude(1.3521)
                .longitude(103.8198)
                .totalLots(100)
                .availableLots(10)
                .distance(0.5)
                .build();

        List<CarParkResponse> expectedResponse = Arrays.asList(carPark);

        when(carParkService.findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<?> response = carParkController.getNearestCarParks(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(carParkService).findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        );
    }

    @Test
    @DisplayName("Should handle different coordinate values")
    void testGetNearestCarParks_DifferentCoordinates() {
        // Given
        NearestCarParksRequest request = new NearestCarParksRequest();
        request.setLatitude(-1.3521); // Negative latitude
        request.setLongitude(180.0);  // Edge longitude
        request.setPage(1);
        request.setPerPage(10);

        List<CarParkResponse> expectedResponse = Collections.emptyList();

        when(carParkService.findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        )).thenReturn(expectedResponse);

        // When
        ResponseEntity<?> response = carParkController.getNearestCarParks(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(carParkService).findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        );
    }
}
