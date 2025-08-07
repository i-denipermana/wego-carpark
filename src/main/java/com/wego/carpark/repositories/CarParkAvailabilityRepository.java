package com.wego.carpark.repositories;


import com.wego.carpark.entities.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, String> {
    Optional<CarParkAvailability> findByCarPark_Id(String carParkId);
    List<CarParkAvailability> findByAvailableLotsGreaterThan(int minAvailableLots);
}
