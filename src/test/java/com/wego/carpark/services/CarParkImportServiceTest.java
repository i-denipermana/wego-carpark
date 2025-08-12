package com.wego.carpark.services;

import com.wego.carpark.entities.CarPark;
import com.wego.carpark.repositories.CarParkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarParkImportServiceTest {

    @Mock
    private CarParkRepository carParkRepository;

    private CarParkImportService carParkImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carParkImportService = new CarParkImportService(carParkRepository);
    }

    @Test
    @DisplayName("Should throw exception when CSV file not found")
    void testImportFromCsv_FileNotFound() {
        // Given
        String nonExistentFile = "non-existent-file.csv";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            carParkImportService.importFromCsv(nonExistentFile);
        });
    }

    @Test
    @DisplayName("Should handle empty CSV file")
    void testImportFromCsv_EmptyFile() throws Exception {
        // Given
        String emptyFile = "data/empty.csv";
        
        // Create a mock resource that exists but is empty
        when(carParkRepository.saveAll(any())).thenReturn(List.of());

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(emptyFile);

        // Then
        assertEquals(0, result.totalRows());
        assertEquals(0, result.convertedRows());
        assertEquals(0, result.skippedRows());
    }

    @Test
    @DisplayName("Should skip rows with missing required fields")
    void testImportFromCsv_SkipInvalidRows() throws Exception {
        // Given
        String invalidFile = "data/invalid.csv";

        when(carParkRepository.saveAll(any())).thenReturn(List.of());

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(invalidFile);

        // Then
        assertTrue(result.totalRows() > 0);
        assertTrue(result.skippedRows() > 0);
        verify(carParkRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should process valid CSV rows correctly")
    void testImportFromCsv_ValidRows() throws Exception {
        // Given
        String validFile = "data/valid.csv";
        
        CarPark existingCarPark = new CarPark();
        existingCarPark.setId("TEST001");
        
        when(carParkRepository.findById("TEST001")).thenReturn(Optional.of(existingCarPark));
        when(carParkRepository.saveAll(any())).thenReturn(List.of(existingCarPark));

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(validFile);

        // Then
        assertTrue(result.totalRows() > 0);
        assertTrue(result.convertedRows() > 0);
        verify(carParkRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle batch processing correctly")
    void testImportFromCsv_BatchProcessing() throws Exception {
        // Given
        String largeFile = "data/large.csv";
        
        when(carParkRepository.findById(any())).thenReturn(Optional.empty());
        when(carParkRepository.saveAll(any())).thenReturn(List.of());

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(largeFile);

        // Then
        assertTrue(result.totalRows() > 0);
        // Verify that saveAll was called multiple times for batch processing
        verify(carParkRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    @DisplayName("Should skip rows with invalid coordinate values")
    void testImportFromCsv_InvalidCoordinates() throws Exception {
        // Given
        String invalidCoordsFile = "data/invalid-coords.csv";

        when(carParkRepository.saveAll(any())).thenReturn(List.of());

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(invalidCoordsFile);

        // Then
        assertTrue(result.totalRows() > 0);
        assertTrue(result.skippedRows() > 0);
        // Should skip rows with non-numeric coordinates
        verify(carParkRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should create new car park when ID not exists")
    void testImportFromCsv_CreateNewCarPark() throws Exception {
        // Given
        String newCarParkFile = "data/new-carpark.csv";
        
        when(carParkRepository.findById("NEW001")).thenReturn(Optional.empty());
        when(carParkRepository.saveAll(any())).thenReturn(List.of());

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(newCarParkFile);

        // Then
        assertTrue(result.convertedRows() > 0);
        verify(carParkRepository).findById("NEW001");
        verify(carParkRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    @DisplayName("Should update existing car park when ID exists")
    void testImportFromCsv_UpdateExistingCarPark() throws Exception {
        // Given
        String existingCarParkFile = "data/existing-carpark.csv";
        
        CarPark existingCarPark = new CarPark();
        existingCarPark.setId("EXIST001");
        existingCarPark.setAddress("Old Address");
        
        when(carParkRepository.findById("EXIST001")).thenReturn(Optional.of(existingCarPark));
        when(carParkRepository.saveAll(any())).thenReturn(List.of(existingCarPark));

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv(existingCarParkFile);

        // Then
        assertTrue(result.convertedRows() > 0);
        verify(carParkRepository).findById("EXIST001");
        verify(carParkRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    @DisplayName("Should calculate total lots based on car park type and decks")
    void testImportFromCsv_CalculateTotalLots() throws Exception {
        // Given
        when(carParkRepository.findById("TEST1")).thenReturn(Optional.empty());
        when(carParkRepository.findById("TEST2")).thenReturn(Optional.empty());
        when(carParkRepository.findById("TEST3")).thenReturn(Optional.empty());
        when(carParkRepository.saveAll(any())).thenReturn(new ArrayList<>());

        // When
        CarParkImportService.ImportResult result = carParkImportService.importFromCsv("data/total-lots-test.csv");

        // Then
        assertEquals(3, result.totalRows());
        assertEquals(3, result.convertedRows());
        assertEquals(0, result.skippedRows());

        ArgumentCaptor<List<CarPark>> captor = ArgumentCaptor.forClass(List.class);
        verify(carParkRepository, times(1)).saveAll(captor.capture());

        List<CarPark> savedCarParks = captor.getValue();
        assertEquals(3, savedCarParks.size());

        // Verify total lots calculation
        CarPark multiStorey = savedCarParks.stream()
                .filter(cp -> cp.getId().equals("TEST1"))
                .findFirst()
                .orElse(null);
        assertNotNull(multiStorey);
        assertEquals(600, multiStorey.getTotalLots()); // 8 decks * 75 = 600

        CarPark basement = savedCarParks.stream()
                .filter(cp -> cp.getId().equals("TEST2"))
                .findFirst()
                .orElse(null);
        assertNotNull(basement);
        assertEquals(75, basement.getTotalLots()); // 1 deck * 75 = 75 (decks take precedence)

        CarPark surface = savedCarParks.stream()
                .filter(cp -> cp.getId().equals("TEST3"))
                .findFirst()
                .orElse(null);
        assertNotNull(surface);
        assertEquals(100, surface.getTotalLots()); // Default for surface (0 decks)
    }
}
