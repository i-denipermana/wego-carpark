package com.wego.carpark.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    private final CarparkProperties carparkProperties;

    public RateLimitConfig(CarparkProperties carparkProperties) {
        this.carparkProperties = carparkProperties;
    }

    @Bean
    public Bucket rateLimitBucket() {
        int requestsPerMinute = carparkProperties.getRateLimit().getRequestsPerMinute();

        Bandwidth limit = BandwidthBuilder.builder()
                .capacity(requestsPerMinute)
                .refillIntervally(requestsPerMinute, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
