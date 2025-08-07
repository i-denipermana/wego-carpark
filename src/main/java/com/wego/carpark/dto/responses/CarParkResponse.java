package com.wego.carpark.dto.responses;

import lombok.*;

@Getter
@Setter
@Builder
public class CarParkResponse {
    private String address;
    private double latitude;
    private double longitude;
    private int totalLots;
    private int availableLots;
    private double distance;
}
