package com.wego.carpark.dto.requests;

import java.util.List;

public class AvailabilityApiDTO {
    private List<Item> items;
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item {
        private String timestamp;
        private List<CarparkData> carpark_data;
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public List<CarparkData> getCarpark_data() { return carpark_data; }
        public void setCarpark_data(List<CarparkData> carpark_data) { this.carpark_data = carpark_data; }
    }

    public static class CarparkData {
        private String carpark_number;
        private String update_datetime;
        private List<CarparkInfo> carpark_info;
        public String getCarpark_number() { return carpark_number; }
        public void setCarpark_number(String carpark_number) { this.carpark_number = carpark_number; }
        public String getUpdate_datetime() { return update_datetime; }
        public void setUpdate_datetime(String update_datetime) { this.update_datetime = update_datetime; }
        public List<CarparkInfo> getCarpark_info() { return carpark_info; }
        public void setCarpark_info(List<CarparkInfo> carpark_info) { this.carpark_info = carpark_info; }
    }

    public static class CarparkInfo {
        private String total_lots;
        private String lot_type;
        private String lots_available;
        public String getTotal_lots() { return total_lots; }
        public void setTotal_lots(String total_lots) { this.total_lots = total_lots; }
        public String getLot_type() { return lot_type; }
        public void setLot_type(String lot_type) { this.lot_type = lot_type; }
        public String getLots_available() { return lots_available; }
        public void setLots_available(String lots_available) { this.lots_available = lots_available; }
    }
}
