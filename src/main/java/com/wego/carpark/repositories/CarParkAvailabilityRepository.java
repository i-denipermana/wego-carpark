package com.wego.carpark.repositories;

import com.wego.carpark.entities.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, Long> {
    List<CarParkAvailability> findByAvailableLotsGreaterThan(int minAvailableLots);
    @Modifying
    @Query(value = """
        INSERT INTO car_park_availability (car_park_id, available_lots, last_updated)
        VALUES (?1, ?2, ?3)
        ON CONFLICT (car_park_id)
        DO UPDATE SET available_lots = EXCLUDED.available_lots, last_updated = EXCLUDED.last_updated
        """, nativeQuery = true)
    int upsert(String carParkId, int availableLots, Instant lastUpdated);
}
