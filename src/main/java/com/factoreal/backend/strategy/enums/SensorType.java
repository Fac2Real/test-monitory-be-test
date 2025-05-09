package com.factoreal.backend.strategy.enums;

public enum SensorType {
    current,
    dust,
    temp,
    humid,
    vibration,
    voc;
    public static SensorType getSensorType(String sensorType){
        return SensorType.valueOf(sensorType);
    }
}