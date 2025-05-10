package com.factoreal.backend.service;

import com.factoreal.backend.dto.LogType;
import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.entity.AbnormalLog;
import com.factoreal.backend.entity.Zone;
import com.factoreal.backend.repository.AbnLogRepository;
import com.factoreal.backend.strategy.RiskMessageProvider;
import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AbnormalLogService {
    private final AbnLogRepository abnLogRepository;
    private final ZoneService zoneService;
    private final RiskMessageProvider riskMessageProvider;

    // 알람 객체를 받아와서 로그 객체 생성.
    @Transactional(rollbackOn = Exception.class)
    public AbnormalLog saveAbnormalLogFromKafkaDto(
            SensorKafkaDto sensorKafkaDto,
            SensorType sensorType,
            RiskLevel riskLevel,
            LogType targetType) throws Exception{
        Zone zone = zoneService.getZone(sensorKafkaDto.getZoneId());



        // DTO의 severity (AlarmEvent.RiskLevel)를 Entity RiskLevel로 매핑
//        RiskLevel entityRiskLevel = mapDtoSeverityToEntityRiskLevel(riskLevel);
        // [TODO] 현재는 스프린트 1 웹 푸쉬, 대시보드 히트 맵 알림 로그만 구현되있음. worker, equip 로그용 구현 필요.
        AbnormalLog abnormalLog = AbnormalLog.builder()
                .targetId(sensorKafkaDto.getSensorId())
                .targetType(targetType)
                .abnormalType(riskMessageProvider.getMessage(sensorType,riskLevel))
                .abnVal(sensorKafkaDto.getVal())
                .zone(zone)
                .build();

        return abnLogRepository.save(abnormalLog);
    }
    /**
     * DTO의 AlarmEvent.RiskLevel(Kafka)을 Entity의 RiskLevel로 매핑합니다.
     * Factory에서는 Entity의 RiskLevel을 사용해야 합니다.
     */
//    private RiskLevel mapDtoSeverityToEntityRiskLevel(RiskLevel dtoSeverity) {
//        if (dtoSeverity == null) {
//            return null;
//        }
//        // DTO의 심각도 수준에 따라 Entity RiskLevel 매핑
//        // CRITICAL -> DANGER (높은 위험)
//        // WARNING, INFO -> WARN (낮은 위험/정보)
//        return switch (dtoSeverity) {
//            case CRITICAL -> RiskLevel.CRITICAL;
//            case WARNING, INFO -> RiskLevel.WARNING;
//            default -> {
//                log.warn("Unknown AlarmEvent DTO severity received: {}. Mapping to WARN.", dtoSeverity);
//                yield RiskLevel.WARNING; // 알 수 없는 값은 기본 WARN으로 처리
//            }
//        };
//    }
}
