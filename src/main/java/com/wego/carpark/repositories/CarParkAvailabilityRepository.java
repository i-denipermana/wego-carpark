package com.wego.carpark.repositories;

import com.wego.carpark.entities.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, Long> {
    Optional<CarParkAvailability> findByCarPark_Id(String carParkId);
    List<CarParkAvailability> findByAvailableLotsGreaterThan(int minAvailableLots);
    @Modifying
    @Query(value = """
        INSERT INTO car_park_availability (car_park_id, available_lots, last_updated)
        VALUES (?1, ?2, ?3)
        ON CONFLICT (car_park_id)
        DO UPDATE SET available_lots = EXCLUDED.available_lots, last_updated = EXCLUDED.last_updated
        """, nativeQuery = true)
    int upsert(String carParkId, int availableLots, Instant lastUpdated);

    // New method for spatial queries - find car parks within bounding box
    @Query("SELECT cpa FROM CarParkAvailability cpa " +
           "JOIN cpa.carPark cp " +
           "WHERE cpa.availableLots > 0 " +
           "AND cp.latitude BETWEEN :minLat AND :maxLat " +
           "AND cp.longitude BETWEEN :minLng AND :maxLng")
    List<CarParkAvailability> findAvailableCarParksInBoundingBox(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng
    );
}
