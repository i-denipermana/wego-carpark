package com.wego.carpark.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AvailabilityApiDTO {
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private String timestamp;
        @JsonProperty("carpark_data")
        private List<CarparkData> carparkData;
    }

    @Setter
    @Getter
    public static class CarparkData {
        @JsonProperty("carpark_number")
        private String carparkNumber;
        @JsonProperty("update_datetime")
        private String updateDatetime;
        @JsonProperty("carpark_info")
        private List<CarparkInfo> carparkInfo;

    }

    @Setter
    @Getter
    public static class CarparkInfo {
        @JsonProperty("total_lots")
        private String totalLots;
        @JsonProperty("lot_type")
        private String lotType;
        @JsonProperty("lots_available")
        private String lotsAvailable;

    }
}
