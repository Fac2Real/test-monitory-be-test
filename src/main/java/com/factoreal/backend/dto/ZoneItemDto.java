package com.factoreal.backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class ZoneItemDto {
    @JsonProperty("title")
    private String title;

    @JsonProperty("env_sensor")
    private List<SensorDto> envSensor;
    @JsonProperty("facility")
    private List<FacilityDto> facility;
}
