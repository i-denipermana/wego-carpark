package com.wego.carpark.services;

import com.wego.carpark.config.CarparkProperties;
import com.wego.carpark.dto.requests.AvailabilityApiDTO;
import com.wego.carpark.repositories.CarParkAvailabilityRepository;
import com.wego.carpark.repositories.CarParkRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CarParkAvailabilityUpdateService {
    private final WebClient webClient;
    private final CarparkProperties carparkProperties;
    private final CarParkRepository carParkRepository;
    private final CarParkAvailabilityRepository availabilityRepository;

    public CarParkAvailabilityUpdateService(
            WebClient webClient,
            CarparkProperties carparkProperties,
            CarParkRepository carParkRepository,
            CarParkAvailabilityRepository availabilityRepository) {
        this.webClient = webClient;
        this.carparkProperties = carparkProperties;
        this.carParkRepository = carParkRepository;
        this.availabilityRepository = availabilityRepository;
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    @Transactional
    public Result updateOnce() {
        AvailabilityApiDTO dto = webClient.get()
                .uri(carparkProperties.getAvailabilityUrl())
                .retrieve()
                .bodyToMono(AvailabilityApiDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(300)))
                .block();

        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            return new Result(0, 0, 0, "empty-payload");
        }

        var item = dto.getItems().get(0); // latest snapshot
        Instant ts = parseInstantSafe(item.getTimestamp()); // ok if null

        AtomicInteger processed = new AtomicInteger();
        AtomicInteger skippedUnknownCarpark = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();

        item.getCarparkData().forEach(cp -> {
            String id = cp.getCarparkNumber();
            if (id == null || id.isBlank()) return;
            var info = (cp.getCarparkInfo() == null || cp.getCarparkInfo().isEmpty())
                    ? null
                    : cp.getCarparkInfo().get(0);

            int available = parseIntSafe(info != null ? info.getLotsAvailable() : null);

            if (!carParkRepository.existsById(id)) {
                skippedUnknownCarpark.incrementAndGet();
                return;
            }
            if (available < 0) {
                errors.incrementAndGet();
                return;
            }

            try {
                availabilityRepository.upsert(id, available, ts != null ? ts : Instant.now());
                processed.incrementAndGet();
            } catch (Exception ex) {
                errors.incrementAndGet();
            }
        });

        return new Result(processed.get(), skippedUnknownCarpark.get(), errors.get(), "ok");
    }

    private static Instant parseInstantSafe(String s) {
        try { return s == null ? null : Instant.parse(s); } catch (Exception e) { return null; }
    }

    public record Result(int processed, int skippedUnknownCarpark, int errors, String status) {}
}
