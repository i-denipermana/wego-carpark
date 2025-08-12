package com.wego.carpark.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "car_parks", indexes = {
    @Index(name = "idx_carpark_coordinates", columnList = "latitude,longitude"),
    @Index(name = "idx_carpark_address", columnList = "address")
})
@Getter
@Setter
public class CarPark {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private double latitude;
    
    @Column(nullable = false)
    private double longitude;
    
    @Column(nullable = false)
    private int totalLots;

    private Double svy21X;
    private Double svy21Y;
}
