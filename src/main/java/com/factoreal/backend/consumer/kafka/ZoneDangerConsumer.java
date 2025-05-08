package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.sender.WebSocketSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneDangerConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketSender webSocketSender;

    @KafkaListener(topics = { "EQUIPMENT", "ENVIRONMENT" }, groupId = "monitory-consumer-group")
    public void consume(String message) {

        log.info("✅ 수신한 Kafka 메시지: " + message);

        try {
            SensorKafkaDto dto = objectMapper.readValue(message, SensorKafkaDto.class);

            // equipId와 zoneId가 같을 때만 처리 - 공간 센서(not 설비 센서)
            if (dto.getEquipId() != null && dto.getEquipId().equals(dto.getZoneId()) && dto.getZoneId() != null) {

                log.info("▶︎ 위험도 감지 start");
                int dangerLevel = getDangerLevel(dto.getSensorType(), dto.getVal());

                if (dangerLevel > 0) {
                    log.info("⚠️ 위험도 {} 센서 타입 : {} 감지됨. Zone: {}", dangerLevel, dto.getSensorType(), dto.getZoneId());
                    webSocketSender.sendDangerLevel(dto.getZoneId(), dto.getSensorType(), dangerLevel);
                }
            }

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 파싱 실패: {}", message, e);
        }
    }

    private int getDangerLevel(String sensorType, double value) { // 위험도 계산 메서드
        return switch (sensorType) { // 센서 타입에 따른 위험도 계산
            case "temp" -> { // 온도 위험도 기준 (KOSHA: https://www.kosha.or.kr/)
                if (value > 40 || value < -35)        // >40℃ 또는 < -35℃ → 위험 (작업 중단 권고)
                    yield 2;
                else if (value > 30 || value < 25)   // >30℃ 또는 < 25℃ → 주의 (작업 제한 또는 휴식 권고)
                    yield 1;
                else                                 // 25℃ ≤ value ≤ 30℃ → 안전 (권장 18~21℃)
                    yield 0;
            }
            
            case "humid" -> { // 상대습도 위험도 기준 (OSHA, ACGIH TLV®, NIOSH)
                if (value >= 80)             // RH ≥ 80% → 위험
                    yield 2;
                else if (value >= 60)        // 60% ≤ RH < 80% → 주의
                    yield 1;
                else                         // RH < 60% → 안전
                    yield 0;
            }
            
            case "vibration" -> { // 진동 위험도 기준 (ISO 10816-3)
                if (value > 7.1)            // >7.1 mm/s → 위험 (2)
                    yield 2;
                else if (value > 2.8)       // >2.8 mm/s → 주의 (1)
                    yield 1;
                else                        // ≤2.8 mm/s → 안전 (0)
                    yield 0;
            }

            case "current" -> { // 전류 위험도 기준 (KEPCO)
                if (value >= 30)        // ≥30 mA → 위험 (강한 경련, 심실세동 및 사망 위험)
                    yield 2;
                else if (value >= 7)    // ≥7 mA → 주의 (고통 한계 전류, 불수전류)
                    yield 1;
                else                    // <7 mA → 안전 (감지전류 수준)
                    yield 0;
            }

            case "dust" -> { // PM2.5 위험도 기준 (고용노동부)
                if (value >= 150)              // ≥ 150㎍/㎥ → 위험 (2)
                    yield 2;
                else if (value >= 75)          // ≥ 75㎍/㎥ → 주의 (1)
                    yield 1;
                else                            // < 75㎍/㎥ → 안전 (0)
                    yield 0;
            }
            
            // 그 외 센서 타입은 안전
            default -> 0;
        }; // switch 끝
    }
}