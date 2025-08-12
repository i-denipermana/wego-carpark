package com.wego.carpark.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "carpark")
public class CarparkProperties {
    private String availabilityUrl;
    private Cache cache = new Cache();
    private RateLimit rateLimit = new RateLimit();

    @Setter
    @Getter
    public static class Cache {
        private int ttl = 300; // default 5 minutes

    }

    @Setter
    @Getter
    public static class RateLimit {
        private int requestsPerMinute = 100;

    }
}
