package com.factoreal.backend.strategy;

import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;

public interface RiskMessageProvider {
    String getMessage(SensorType sensorType, RiskLevel riskLevel);
}
