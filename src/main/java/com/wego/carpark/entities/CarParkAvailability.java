package com.wego.carpark.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "car_park_availability")
public class CarParkAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_park_id")
    private CarPark carPark;

    private int availableLots;

    private Instant lastUpdated;
}
