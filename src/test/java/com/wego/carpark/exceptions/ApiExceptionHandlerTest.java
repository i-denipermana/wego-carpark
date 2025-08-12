package com.wego.carpark.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    @Test
    @DisplayName("Should handle ImportValidationException correctly")
    void testHandleImportValidation() {
        // Given
        ImportValidationException ex = new ImportValidationException(
                "Invalid CSV format",
                Arrays.asList("car_park_no", "address"),
                Arrays.asList("car_park_no")
        );

        // When
        ResponseEntity<?> response = exceptionHandler.handleImportValidation(ex);

        // Then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("CSV_VALIDATION", body.get("error"));
        assertEquals("Invalid CSV format", body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertNotNull(body.get("missing_headers"));
        assertNotNull(body.get("received_headers"));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException correctly")
    void testHandleIllegalArgument() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");

        // When
        ResponseEntity<?> response = exceptionHandler.handleIllegalArgument(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("BAD_REQUEST", body.get("error"));
        assertEquals("Invalid parameter", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void testHandleGeneric() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<?> response = exceptionHandler.handleGeneric(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("INTERNAL_SERVER_ERROR", body.get("error"));
        assertEquals("Unexpected error", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should include extra fields when provided")
    void testHandleWithExtraFields() {
        // Given
        ImportValidationException ex = new ImportValidationException(
                "Validation failed",
                Arrays.asList("required_field"),
                Arrays.asList("optional_field")
        );

        // When
        ResponseEntity<?> response = exceptionHandler.handleImportValidation(ex);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("missing_headers"));
        assertTrue(body.containsKey("received_headers"));
        assertEquals(Arrays.asList("required_field"), body.get("missing_headers"));
        assertEquals(Arrays.asList("optional_field"), body.get("received_headers"));
    }

    @Test
    @DisplayName("Should not include extra fields when null")
    void testHandleWithoutExtraFields() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Simple error");

        // When
        ResponseEntity<?> response = exceptionHandler.handleIllegalArgument(ex);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertFalse(body.containsKey("missing_headers"));
        assertFalse(body.containsKey("received_headers"));
        assertEquals("Simple error", body.get("message"));
    }

    @Test
    @DisplayName("Should include timestamp in all responses")
    void testTimestampIncluded() {
        // Given
        Exception ex = new Exception("Test error");

        // When
        ResponseEntity<?> response = exceptionHandler.handleGeneric(ex);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertNotNull(body.get("timestamp"));
        assertTrue(body.get("timestamp") instanceof String);
    }
}
