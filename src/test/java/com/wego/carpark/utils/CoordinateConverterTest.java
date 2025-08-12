package com.wego.carpark.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CoordinateConverterTest {

    @Test
    @DisplayName("Should convert SVY21 coordinates to WGS84")
    void testSvy21ToWgs84() {
        // Given - Known SVY21 coordinates in Singapore
        double svy21X = 28947.5;  // SVY21 X coordinate
        double svy21Y = 29208.5;  // SVY21 Y coordinate

        // When
        CoordinateConverter.LatLng result = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);

        // Then
        assertNotNull(result);
        // Expected WGS84 coordinates (approximate)
        // Latitude should be around 1.28° N (actual conversion result)
        assertTrue(result.latitude() > 1.27 && result.latitude() < 1.29, 
                "Latitude should be around 1.28° N, got: " + result.latitude());
        // Longitude should be around 103.8° E
        assertTrue(result.longitude() > 103.7 && result.longitude() < 103.9, 
                "Longitude should be around 103.8° E, got: " + result.longitude());
    }

    @Test
    @DisplayName("Should handle zero coordinates")
    void testSvy21ToWgs84_ZeroCoordinates() {
        // Given
        double svy21X = 0.0;
        double svy21Y = 0.0;

        // When
        CoordinateConverter.LatLng result = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);

        // Then
        assertNotNull(result);
        assertTrue(result.latitude() != 0.0 || result.longitude() != 0.0, 
                "Zero SVY21 coordinates should not result in zero WGS84 coordinates");
    }

    @Test
    @DisplayName("Should handle negative coordinates")
    void testSvy21ToWgs84_NegativeCoordinates() {
        // Given
        double svy21X = -1000.0;
        double svy21Y = -1000.0;

        // When
        CoordinateConverter.LatLng result = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);

        // Then
        assertNotNull(result);
        // Should still produce valid coordinates
        assertTrue(Double.isFinite(result.latitude()));
        assertTrue(Double.isFinite(result.longitude()));
    }

    @Test
    @DisplayName("Should handle large coordinates")
    void testSvy21ToWgs84_LargeCoordinates() {
        // Given
        double svy21X = 100000.0;
        double svy21Y = 100000.0;

        // When
        CoordinateConverter.LatLng result = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);

        // Then
        assertNotNull(result);
        // Should still produce valid coordinates
        assertTrue(Double.isFinite(result.latitude()));
        assertTrue(Double.isFinite(result.longitude()));
    }

    @Test
    @DisplayName("Should produce consistent results for same input")
    void testSvy21ToWgs84_Consistency() {
        // Given
        double svy21X = 28947.5;
        double svy21Y = 29208.5;

        // When
        CoordinateConverter.LatLng result1 = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);
        CoordinateConverter.LatLng result2 = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);

        // Then
        assertEquals(result1.latitude(), result2.latitude(), 0.000001);
        assertEquals(result1.longitude(), result2.longitude(), 0.000001);
    }

    @Test
    @DisplayName("Should handle decimal coordinates")
    void testSvy21ToWgs84_DecimalCoordinates() {
        // Given
        double svy21X = 28947.123;
        double svy21Y = 29208.456;

        // When
        CoordinateConverter.LatLng result = CoordinateConverter.svy21ToWgs84(svy21X, svy21Y);

        // Then
        assertNotNull(result);
        assertTrue(Double.isFinite(result.latitude()));
        assertTrue(Double.isFinite(result.longitude()));
        // Should be different from integer coordinates
        CoordinateConverter.LatLng integerResult = CoordinateConverter.svy21ToWgs84(28947.0, 29208.0);
        assertNotEquals(result.latitude(), integerResult.latitude(), 0.000001);
        assertNotEquals(result.longitude(), integerResult.longitude(), 0.000001);
    }

    @Test
    @DisplayName("Should validate LatLng record structure")
    void testLatLngRecord() {
        // Given
        double latitude = 1.3521;
        double longitude = 103.8198;

        // When
        CoordinateConverter.LatLng latLng = new CoordinateConverter.LatLng(latitude, longitude);

        // Then
        assertEquals(latitude, latLng.latitude());
        assertEquals(longitude, latLng.longitude());
    }
}
