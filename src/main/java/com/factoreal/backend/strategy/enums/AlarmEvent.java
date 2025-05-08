package com.factoreal.backend.strategy.enums;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Kafka Consumer에서 해당 객체 생성
 * 다양한 채널로 발송될 알람의 내용을 담는 표준 이벤트 객체
 */
public record AlarmEvent(
        // 1. 필수 공통 정보
        UUID eventId,           // 알람 이벤트 고유 ID (추적용) -> BE에서 할당
        String abnormalType,       // 알람 종류 (예: "HIGH_HEART_RATE", "LOW_BATTERY", "SERVER_DOWN") - 분류 및 라우팅에 사용
        float abnormalValue, // 이상치 값
        RiskLevel riskLevel, // 알람 심각도 (예: CRITICAL, WARNING, INFO) - 채널 선택, 표현 방식 결정에 사용

        Timestamp timestamp,      // 알람 발생 시각 -> BE에서 할당

        // 2. 수신 대상 정보 (하나 이상일 수 있음)
        // 기본 : 대시보드(웹 푸시)
        // 공간내 작업자 : 웨어러블(앱 푸시)
//        List<RecipientInfo> recipients, // 알람을 받을 대상 목록 -> 각 로직에서 구현하기

        // 3. 내용 정보 (프로토콜별로 활용 방식이 다름)
        String title,           // 알람 제목 (푸시 제목, 이메일 제목 등에 활용)
        String messageBody,     // 알람 본문 (푸시 내용, SMS 내용, 이메일 본문 등에 활용)
//        Map<String, Object> data, // 구조화된 데이터 페이로드 (앱 내부 처리용 데이터, MQTT/WebSocket 페이로드, 푸시 데이터 페이로드 등)

        // 4. 부가 정보 (선택적)
        String source          // 알람 발생 출처 (예: "WearableSensorService", "BatchJobMonitor")
//        Map<String, String> metadata // 기타 메타데이터 (예: 관련 URL, 담당자 정보 등)
) {

    /**
     * 알람 수신 대상 정보
     */
//    public record RecipientInfo(
//            RecipientType type, // 대상 타입
//            String value        // 대상 값 (예: 유저 ID, 디바이스 토큰, 토픽 이름, 이메일 주소 등)
//    ) {}

    /**
     * 수신 대상 타입 정의
     */
//    public enum RecipientType {
//        USER_ID,          // 특정 사용자 ID (이 경우, 해당 유저의 등록된 모든 기기/채널로 발송 로직 필요)
//        DEVICE_TOKEN,     // FCM/APNS 같은 특정 디바이스 토큰
//        TOPIC,            // MQTT 토픽 이름
//        EMAIL_ADDRESS,    // 이메일 주소
//        SMS_NUMBER,       // SMS 수신 번호
//        WEBSOCKET_SESSION, // 특정 웹소켓 세션 ID
//        HTTP_ENDPOINT      // 알람을 받을 HTTP Webhook URL
//        // 필요에 따라 추가...
//    }

    /**
     * 알람 심각도 정의
     */
//    public enum RiskLevel {
//        CRITICAL, // 2: 즉시 조치 필요
//        WARNING,  // 1: 주의 필요
//        INFO      // 0: 단순 정보
//    }
}