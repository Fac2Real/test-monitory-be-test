package com.factoreal.backend.service.strategy;

import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.NotificationStrategy;
import com.factoreal.backend.strategy.NotificationStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationStrategyFactoryTest {
    private NotificationStrategy appStrategy;
    private NotificationStrategy webStrategy;
    private NotificationStrategy smsStrategy;

    private NotificationStrategyFactory factory;
    //https://techblog.woowahan.com/2638/를 참고함.
    @BeforeEach
    void setUp(){
        // Mock 마다 strategy 생성
        appStrategy = mock(NotificationStrategy.class);
        webStrategy = mock(NotificationStrategy.class);
        smsStrategy = mock(NotificationStrategy.class);

        // 각 전략이 지원하는 RiskLevel 설정
        when(appStrategy.getSupportedLevel()).thenReturn(RiskLevel.WARNING);
        when(webStrategy.getSupportedLevel()).thenReturn(RiskLevel.WARNING);
        when(smsStrategy.getSupportedLevel()).thenReturn(RiskLevel.CRITICAL);

        // factory 빈을 초기화할 때 app, web, sms를 주입하는 로직.
        factory = new NotificationStrategyFactory(List.of(appStrategy,webStrategy,smsStrategy));
    }

    @Test
    void testGetStrategyForWarn(){
        List<NotificationStrategy> result = factory.getStrategiesForLevel(RiskLevel.WARNING);
        // for (NotificationStrategy strategy : result) {
        //     System.out.println("Strategy: " + strategy + ", Level: " + strategy.getSupportedLevel());
        // }

        // WARN 위험 단계에서 app과 web 알람 객체가 제대로 반환되는지 확인
        assertEquals(2,result.size());
        assertTrue(result.contains(appStrategy));
        assertTrue(result.contains(webStrategy));
    }

    @Test
    void testGetStrategyForDanger(){
        List<NotificationStrategy> result = factory.getStrategiesForLevel(RiskLevel.CRITICAL);
        // for (NotificationStrategy strategy : result) {
        //     System.out.println("Strategy: " + strategy + ", Level: " + strategy.getSupportedLevel());
        // }

        // Danger 위험 단계에서 WARN 위험 단계의 객체 + sms 알람 객체가 제대로 반환되는지 확인
        assertEquals(3,result.size());
        assertTrue(result.contains(appStrategy));
        assertTrue(result.contains(webStrategy));
        assertTrue(result.contains(smsStrategy));
    }
}
