package com.factoreal.backend.strategy.enums;

import com.factoreal.backend.dto.SensorDataDto;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public enum SensorRule {
    TEMP_RULE(SensorType.temp, data -> data.getVal() > 35.0, AlarmType.HIGH_TEMP, RiskLevel.WARNING),
    DUST_RULE(SensorType.dust, data -> data.getVal() > 40.0, AlarmType.HIGH_DUST, RiskLevel.CRITICAL),
    HUMID_RULE(SensorType.humid, data -> data.getVal() < 30.0, AlarmType.LOW_HUMIDITY, RiskLevel.WARNING),
    VIBRATION_RULE(SensorType.vibration, data -> data.getVal() > 7.0, AlarmType.HIGH_VIBRATION, RiskLevel.WARNING),
    VOC_RULE(SensorType.voc, data -> data.getVal() > 35.0, AlarmType.VOC_DETECTED, RiskLevel.CRITICAL),
    CURRENT_RULE(SensorType.current, data -> data.getVal() > 7.0, AlarmType.OVER_CURRENT, RiskLevel.CRITICAL);

    private final SensorType sensorType;
    private final Predicate<SensorDataDto> condition;
    private final AlarmType alarmType;
    private final RiskLevel riskLevel;

    SensorRule(SensorType sensorType, Predicate<SensorDataDto> condition, AlarmType alarmType, RiskLevel riskLevel) {
        this.sensorType = sensorType;
        this.condition = condition;
        this.alarmType = alarmType;
        this.riskLevel = riskLevel;
    }

    public boolean supports(SensorDataDto data) {
        return Objects.equals(data.getSensorType(), sensorType.name());
    }

    public Optional<AlarmEvent> evaluate(SensorDataDto data) {
        if (supports(data) && condition.test(data)) {
            String source = (data.getEquipId() == null || data.getEquipId().isBlank())
                    ? "Sensor"
                    : "Equip";
            return Optional.of(new AlarmEvent(
                    UUID.randomUUID(),
                    alarmType.name(),
                    data.getVal(),
                    riskLevel,
                    null,
                    alarmType.getTitle(),
                    alarmType.getMessage(),
                    // 이곳에 sensorData의 equip_id가 없으면 Sensor, equip_id가 있으면 Equip
                    source
            ));
        }
        return Optional.empty();
    }

    private static double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}