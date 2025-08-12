package com.wego.carpark.dto.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearestCarParksRequest {
    @NotNull(message = "Latitude is required")
    @Min(value = -90, message = "Latitude must be between -90 and 90 degrees")
    @Max(value = 90, message = "Latitude must be between -90 and 90 degrees")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Min(value = -180, message = "Longitude must be between -180 and 180 degrees")
    @Max(value = 180, message = "Longitude must be between -180 and 180 degrees")
    private Double longitude;

    @Min(value = 1, message = "Page number must be at least 1")
    private int page = 1;

    @Min(value = 1, message = "Items per page must be at least 1")
    @Max(value = 100, message = "Items per page cannot exceed 100")
    private int perPage = 10;
}
