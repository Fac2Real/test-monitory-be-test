package com.factoreal.backend.dto;

import com.factoreal.backend.entity.Zone;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorDto {
    private String sensorId; // 센서ID
    private String sensorType; // 센서종류
    private String zoneId; // zoneId 저장
    private String equipId;
}