package com.factoreal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SensorUpdateDto {
    private String sensorPurpose;  // 센서목적
    private String location;       // 위치
    private Integer threshold;     // 임계치

    public SensorUpdateDto() {}
}