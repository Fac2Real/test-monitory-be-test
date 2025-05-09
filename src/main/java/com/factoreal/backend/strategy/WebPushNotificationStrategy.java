package com.factoreal.backend.strategy;

import com.factoreal.backend.strategy.enums.AlarmEventDto;
import com.factoreal.backend.strategy.enums.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("WEB")
// https://stir.tistory.com/516를 참고하여 구현
public class WebPushNotificationStrategy implements NotificationStrategy {
    @Override
    public void send(AlarmEventDto alarmEventDto) {
        log.info("🍿 Web Push Notification Strategy");
    }

    @Override
    public RiskLevel getSupportedLevel() {
        return RiskLevel.INFO;
    }
}
