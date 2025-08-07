package com.wego.carpark.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "car_parks")
@Getter
@Setter
public class CarPark {
    @Id
    private String id;
    private String address;
    private double latitude;
    private double longitude;
    private int totalLots;
}
