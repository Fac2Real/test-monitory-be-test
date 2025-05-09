package com.factoreal.backend.strategy;

import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;
import org.springframework.stereotype.Component;

@Component
public class DefaultRiskMessageProvider implements RiskMessageProvider {
    @Override
    public String getMessage(SensorType sensorType, RiskLevel riskLevel) {
        return switch (sensorType) {
            case temp -> switch (riskLevel) {
                case INFO -> "온도가 정상 범위입니다.";
                case WARNING -> "고온 주의! 온도가 35도를 초과했습니다.";
                case CRITICAL -> "위험! 온도가 50도를 초과했습니다.";
            };
            case humid -> switch (riskLevel) {
                case INFO -> "습도가 정상 범위입니다.";
                case WARNING -> "건조 주의! 습도가 30% 미만입니다.";
                case CRITICAL -> "위험! 습도가 15% 이하입니다.";
            };
            case vibration -> switch (riskLevel) {
                case INFO -> "진동이 정상 범위입니다.";
                case WARNING -> "진동 주의! 5 이상 감지됨.";
                case CRITICAL -> "위험! 진동이 10 이상으로 매우 심합니다.";
            };
            case dust -> switch (riskLevel) {
                case INFO -> "먼지 농도 정상.";
                case WARNING -> "주의! 먼지 농도가 다소 높습니다.";
                case CRITICAL -> "위험! 먼지 농도가 40 이상입니다.";
            };
            case voc -> switch (riskLevel) {
                case INFO -> "VOC 농도 정상.";
                case WARNING -> "VOC 경고! 공기 중 오염 물질이 감지됨.";
                case CRITICAL -> "위험! VOC 농도가 높습니다. 환기 필요.";
            };
            case current -> switch (riskLevel) {
                case INFO -> "전류 정상.";
                case WARNING -> "과전류 주의! 5A 초과 감지.";
                case CRITICAL -> "위험! 과전류가 7A를 초과했습니다.";
            };
        };
    }
}
