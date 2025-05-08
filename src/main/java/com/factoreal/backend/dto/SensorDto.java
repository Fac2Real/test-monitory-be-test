package com.factoreal.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorDto { // BE -> FE 용 DTO
    private String sensorId; // 센서ID
    private String sensorType; // 센서종류
    private String zoneId; // zoneId 저장
    private String equipId;
}