package com.wego.carpark.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ImportValidationException.class)
    public ResponseEntity<?> handleImportValidation(ImportValidationException ex) {
        Map<String, Object> extra = Map.of(
                "missing_headers", ex.getMissingHeaders(),
                "received_headers", ex.getReceivedHeaders()
        );
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "CSV_VALIDATION", ex.getMessage(), extra);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", ex.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message, Map<String, Object> extra) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("error", error);
        body.put("message", message);
        if (extra != null) body.putAll(extra);
        return ResponseEntity.status(status).body(body);
    }
}
