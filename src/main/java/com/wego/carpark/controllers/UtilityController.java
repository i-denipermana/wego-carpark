package com.wego.carpark.controllers;

import com.wego.carpark.services.CarParkAvailabilityUpdateService;
import com.wego.carpark.services.CarParkImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/util")
public class UtilityController {
    private final CarParkImportService importService;
    private final CarParkAvailabilityUpdateService carParkAvailabilityUpdateService;

    public UtilityController(CarParkImportService importService, CarParkAvailabilityUpdateService carParkAvailabilityUpdateService) {
        this.importService = importService;
        this.carParkAvailabilityUpdateService = carParkAvailabilityUpdateService;
    }

    @GetMapping("/import-carparks")
    public ResponseEntity<?> importCarParks(
            @RequestParam(defaultValue = "data/carparks.csv") String path) {
        try {
            var result = importService.importFromCsv(path);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }

    @GetMapping("/update-availability")
    public ResponseEntity<?> updateAvailability() {
        var result = carParkAvailabilityUpdateService.updateOnce();
        return ResponseEntity.ok(result);
    }
}
