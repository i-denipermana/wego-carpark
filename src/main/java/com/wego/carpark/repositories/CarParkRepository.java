package com.wego.carpark.repositories;

import com.wego.carpark.entities.CarPark;
import org.springframework.data.repository.CrudRepository;

public interface CarParkRepository extends CrudRepository<CarPark, String> {
}
