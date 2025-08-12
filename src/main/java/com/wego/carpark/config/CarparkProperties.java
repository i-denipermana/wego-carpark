package com.wego.carpark.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "carpark")
public class CarparkProperties {
    private String availabilityUrl;
    public String getAvailabilityUrl() { return availabilityUrl; }
    public void setAvailabilityUrl(String v) { this.availabilityUrl = v; }
}
