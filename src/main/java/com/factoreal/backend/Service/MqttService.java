package com.factoreal.backend.Service;

import com.factoreal.backend.Dto.SensorDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {
    private final MqttClient mqttClient;
    private final SensorService sensorService;

    /**
     * - 디바이스의 shadow 메타데이터 변경사항(등록/수정)을 구독
     * @throws MqttException mqtt 연결에 실패시 MqttException 발생
     */
    @PostConstruct
    public void SensorShadowSubscription() throws MqttException {
        // 🟢 구독할 Thing 설정
        String thingName = "Sensor";
        // #는 topic의 여러 level을 대체 가능, +는 topic의 단일 level을 대체 가능
        String topic = "$aws/things/" + thingName + "/shadow/name/+/update/documents";
        mqttClient.subscribe(topic,1,  (t, msg) -> {
            String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);

            // JSON 파싱 및 DB 저장은 이후 구현 예정
            try{
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(payload);
                log.info("📥 MQTT 수신 (topic: {}): {}", t, jsonNode);
                String sensorId = jsonNode.at("/id").asText();
                String type = jsonNode.at("/type").asText();
                SensorDto dto = new SensorDto(sensorId, type);
                sensorService.saveSensor(dto); // 중복이면 예외 발생
                log.info("✅ 센서 저장 완료: {}", sensorId);
            } catch (DataIntegrityViolationException e) {
                log.warn("⚠️ 중복 센서 저장 시도 차단됨: {}", e.getMessage());
            } catch (Exception e) {
                log.error("❌ JSON 파싱 또는 저장 중 오류: {}", e.getMessage());
            }

        });
        log.info("📡 MQTT subscribe 완료됨: topic = {}", topic); // ★ subscribe 후에도 로그
    }
}
