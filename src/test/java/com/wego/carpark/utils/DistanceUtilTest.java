package com.wego.carpark.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class DistanceUtilTest {

    @Test
    @DisplayName("Should calculate correct distance between two points")
    void testHaversineDistance() {
        // Test case 1: Same point should return 0 distance
        double distance1 = DistanceUtil.haversine(1.0, 1.0, 1.0, 1.0);
        assertEquals(0.0, distance1, 0.001, "Distance between same points should be 0");

        // Test case 2: Known distance calculation
        // Singapore coordinates: 1.3521° N, 103.8198° E
        // Kuala Lumpur coordinates: 3.1390° N, 101.6869° E
        // Approximate distance: ~300 km
        double distance2 = DistanceUtil.haversine(1.3521, 103.8198, 3.1390, 101.6869);
        assertTrue(distance2 > 290 && distance2 < 310, 
                "Distance between Singapore and KL should be approximately 300km, got: " + distance2);

        // Test case 3: Short distance calculation
        // Two points in Singapore with known approximate distance
        double distance3 = DistanceUtil.haversine(1.3521, 103.8198, 1.3521, 103.8298);
        assertTrue(distance3 > 0.8 && distance3 < 1.2, 
                "Short distance should be approximately 1km, got: " + distance3);
    }

    @Test
    @DisplayName("Should handle edge cases correctly")
    void testHaversineEdgeCases() {
        // Test with negative coordinates
        double distance1 = DistanceUtil.haversine(-1.0, -1.0, 1.0, 1.0);
        assertTrue(distance1 > 0, "Distance should be positive for different points");

        // Test with very small differences
        double distance2 = DistanceUtil.haversine(1.0, 1.0, 1.000001, 1.000001);
        assertTrue(distance2 > 0, "Distance should be positive even for very small differences");

        // Test with large coordinate differences
        double distance3 = DistanceUtil.haversine(0.0, 0.0, 90.0, 180.0);
        assertTrue(distance3 > 0, "Distance should be positive for large coordinate differences");
    }

    @Test
    @DisplayName("Should be commutative (distance A to B equals distance B to A)")
    void testHaversineCommutative() {
        double lat1 = 1.3521;
        double lon1 = 103.8198;
        double lat2 = 3.1390;
        double lon2 = 101.6869;

        double distanceAB = DistanceUtil.haversine(lat1, lon1, lat2, lon2);
        double distanceBA = DistanceUtil.haversine(lat2, lon2, lat1, lon1);

        assertEquals(distanceAB, distanceBA, 0.001, "Distance should be commutative");
    }
}
