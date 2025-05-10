package com.factoreal.backend.dto;

import com.factoreal.backend.entity.Sensor;
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

    public static SensorDto fromEntity(Sensor sensor) {
        if (sensor == null) return null;

        return SensorDto.builder()
                .sensorId(sensor.getSensorId())
                .sensorType(sensor.getSensorType().toString())
                .zoneId(sensor.getZone().getZoneId())
                .equipId(sensor.getEquip().getEquipId())
                .build();
    }
}