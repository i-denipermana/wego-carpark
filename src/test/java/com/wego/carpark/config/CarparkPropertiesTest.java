package com.wego.carpark.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "carpark.availability-url=https://test-api.example.com/carpark-availability",
        "carpark.cache.ttl=600",
        "carpark.rate-limit.requests-per-minute=200"
})
class CarparkPropertiesTest {

    @Autowired
    private CarparkProperties carparkProperties;

    @Test
    @DisplayName("Should load availability URL from configuration")
    void testAvailabilityUrl() {
        assertEquals("https://test-api.example.com/carpark-availability",
                carparkProperties.getAvailabilityUrl());
    }

    @Test
    @DisplayName("Should load cache TTL from configuration")
    void testCacheTtl() {
        assertEquals(600, carparkProperties.getCache().getTtl());
    }

    @Test
    @DisplayName("Should load rate limit from configuration")
    void testRateLimit() {
        assertEquals(200, carparkProperties.getRateLimit().getRequestsPerMinute());
    }

    @Test
    @DisplayName("Should have default values when not configured")
    void testDefaultValues() {
        CarparkProperties defaultProps = new CarparkProperties();

        // Test default cache TTL
        assertEquals(300, defaultProps.getCache().getTtl());

        // Test default rate limit
        assertEquals(100, defaultProps.getRateLimit().getRequestsPerMinute());
    }

    @Test
    @DisplayName("Should handle nested configuration objects")
    void testNestedConfiguration() {
        assertNotNull(carparkProperties.getCache());
        assertNotNull(carparkProperties.getRateLimit());

        assertTrue(carparkProperties.getCache().getTtl() > 0);
        assertTrue(carparkProperties.getRateLimit().getRequestsPerMinute() > 0);
    }
}
