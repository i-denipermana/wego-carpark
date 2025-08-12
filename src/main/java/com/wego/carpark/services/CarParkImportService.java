package com.wego.carpark.services;

import com.wego.carpark.entities.CarPark;
import com.wego.carpark.repositories.CarParkRepository;
import com.wego.carpark.utils.CoordinateConverter;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarParkImportService {
    private final CarParkRepository carParkRepository;

    public CarParkImportService(CarParkRepository carParkRepository) {
        this.carParkRepository = carParkRepository;
    }

    @Transactional
    public ImportResult importFromCsv(String classpathFile) throws Exception {
        var resource = new ClassPathResource(classpathFile);
        if (!resource.exists()) {
            throw new IllegalArgumentException("CSV file not found on classpath: " + classpathFile);
        }

        try (var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            var format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .setTrim(true)
                    .build();
            try (var parser = new CSVParser(reader, format)) {
                List<CarPark> batch = new ArrayList<>();
                int total = 0, converted = 0, skipped = 0;

                for (CSVRecord rec : parser) {
                    total++;
                    String id = rec.get("car_park_no").trim();
                    String address = rec.get("address").trim();
                    String xStr = rec.get("x_coord");
                    String yStr = rec.get("y_coord");
                    if (id.isEmpty() || address.isEmpty() || xStr == null || yStr == null) {
                        skipped++;
                        continue;
                    }
                    double x, y;
                    try {
                        x = Double.parseDouble(xStr);
                        y = Double.parseDouble(yStr);
                    } catch (NumberFormatException ex) {
                        skipped++;
                        continue;
                    }
                    var latLng = CoordinateConverter.svy21ToWgs84(x, y);
                    converted++;

                    var cp = carParkRepository.findById(id).orElseGet(CarPark::new);
                    cp.setId(id);
                    cp.setAddress(address);
                    cp.setLatitude(latLng.latitude());
                    cp.setLongitude(latLng.longitude());
                    cp.setSvy21X(x);
                    cp.setSvy21Y(y);

                    // Set a reasonable default for total lots based on car park type
                    // This will be updated with actual data from the availability API
                    cp.setTotalLots(calculateDefaultTotalLots(rec));

                    batch.add(cp);
                    if (batch.size() == 500) {
                        carParkRepository.saveAll(batch);
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) carParkRepository.saveAll(batch);

                return new ImportResult(total, converted, skipped);
            }
        }
    }

    private int calculateDefaultTotalLots(CSVRecord rec) {
        try {
            // Try to get car_park_decks first
            String decksStr = rec.get("car_park_decks");
            if (decksStr != null && !decksStr.trim().isEmpty()) {
                int decks = Integer.parseInt(decksStr.trim());
                if (decks > 0) {
                    // Estimate: each deck typically has 50-100 lots
                    return decks * 75; // Average of 75 lots per deck
                }
            }

            // Fallback: estimate based on car park type
            String carParkType = rec.get("car_park_type");
            if (carParkType != null) {
                return switch (carParkType.toUpperCase()) {
                    case "MULTI-STOREY CAR PARK" -> 200; // Typical multi-storey car park
                    case "BASEMENT CAR PARK" -> 150; // Typical basement car park
                    case "SURFACE CAR PARK" -> 100; // Typical surface car park
                    default -> 100; // Default fallback
                };
            }
        } catch (Exception e) {
            // If any parsing fails, use default
        }

        return 100; // Default fallback
    }

    public record ImportResult(int totalRows, int convertedRows, int skippedRows) {}
}
