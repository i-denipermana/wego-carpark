package com.wego.carpark.controllers;

import com.wego.carpark.dto.requests.NearestCarParksRequest;
import com.wego.carpark.dto.responses.CarParkResponse;
import com.wego.carpark.services.CarParkService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carparks")
public class CarParkController {
    private CarParkService carParkService;

    public CarParkController(CarParkService carParkService) {
        this.carParkService = carParkService;
    }

    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestCarParks(
            @Valid @ModelAttribute NearestCarParksRequest request // Automatically validated
    ) {
        List<CarParkResponse> result = carParkService.findNearestCarParks(
                request.getLatitude(),
                request.getLongitude(),
                request.getPage(),
                request.getPerPage()
        );
        return ResponseEntity.ok(result);
    }
}
