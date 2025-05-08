package com.factoreal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Kafka mapper용 DTO
public class SensorKafkaDto {
    private String zoneId;
    private String equipId;
    private String sensorId;
    private String sensorType;
    private Double val;  // 측정값 단위
    private String time; // 센서 생성시간
}
