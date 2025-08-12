package com.wego.carpark.services;

import com.wego.carpark.dto.responses.CarParkResponse;
import com.wego.carpark.entities.CarPark;
import com.wego.carpark.entities.CarParkAvailability;
import com.wego.carpark.repositories.CarParkAvailabilityRepository;
import com.wego.carpark.utils.DistanceUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class CarParkService {

    private final CarParkAvailabilityRepository carParkAvailabilityRepository;

    public CarParkService(CarParkAvailabilityRepository carParkAvailabilityRepository) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
    }

    public List<CarParkResponse> findNearestCarParks(double latitude, double longitude, int page, int perPage) {
        List<CarParkAvailability> availableList = carParkAvailabilityRepository.findByAvailableLotsGreaterThan(0);

        List<CarParkResponse> dtos = availableList.stream()
                .map(availability -> {
                    CarPark carPark = availability.getCarPark();
                    double distance = DistanceUtil.haversine(latitude, longitude, carPark.getLatitude(), carPark.getLongitude());
                    return CarParkResponse.builder()
                            .address(carPark.getAddress())
                            .latitude(carPark.getLatitude())
                            .longitude(carPark.getLongitude())
                            .totalLots(carPark.getTotalLots())
                            .availableLots(availability.getAvailableLots())
                            .distance(distance)
                            .build();
                }).sorted(Comparator.comparingDouble(CarParkResponse::getDistance)).toList();

        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, dtos.size());
        if (fromIndex > toIndex) return Collections.emptyList();
        return dtos.subList(fromIndex, toIndex);
    }
}
