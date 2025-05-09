package com.factoreal.backend.strategy;

import com.factoreal.backend.strategy.enums.AlarmEventDto;
import com.factoreal.backend.strategy.enums.RiskLevel;

public interface NotificationStrategy {
    void send(AlarmEventDto alarmEventDto);
    // 이 인터페이스를 상속받는 객체가 동작할 위험 레벨을 설정.
    RiskLevel getSupportedLevel();
}
