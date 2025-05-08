package com.factoreal.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDataDto { // Kafka -> BE 용 Dto
    private String zoneId;
    private String equipId;
    private String sensorId;
    private String sensorType;
    private Float val;
    private String time;
}
