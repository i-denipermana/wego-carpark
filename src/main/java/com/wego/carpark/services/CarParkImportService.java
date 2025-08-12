package com.wego.carpark.services;

import com.wego.carpark.entities.CarPark;
import com.wego.carpark.exceptions.ImportValidationException;
import com.wego.carpark.repositories.CarParkRepository;
import com.wego.carpark.utils.CoordinateConverter;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
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
    private static final List<String> REQUIRED_HEADERS = List.of("car_park_no", "address", "x_coord", "y_coord");
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


    private static String getOrNull(CSVRecord r, String col) {
        return r.isMapped(col) ? r.get(col) : null;
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    public record ImportResult(int totalRows, int convertedRows, int skippedRows) {}

}
