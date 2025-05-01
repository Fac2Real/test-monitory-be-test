-- ========================
-- V1__init.sql
-- 최초 테이블 및 제약 조건 생성
-- ========================

CREATE TABLE zone_info (
    zone_id VARCHAR(100) NOT NULL,
    zone_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (zone_id)
);

CREATE TABLE role_info (
    role_id VARCHAR(50) NOT NULL,
    zone_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (role_id, zone_id),
    CONSTRAINT FK_zone_info_TO_role_info_1 FOREIGN KEY (zone_id) REFERENCES zone_info (zone_id)
);

CREATE TABLE worker_info (
    worker_id VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    phone_number VARCHAR(50),
    email VARCHAR(100),
    role_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (worker_id)
);

CREATE TABLE wearable_info (
    wearable_id VARCHAR(100) NOT NULL,
    created_id TIMESTAMP NOT NULL,
    PRIMARY KEY (wearable_id)
);

CREATE TABLE wearable_hist (
    id BIGINT NOT NULL,
    wearable_id VARCHAR(100) NOT NULL,
    worker_id VARCHAR(100) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    use_flag INT COMMENT '1: 사용중\n0: 미사용중',
    zone_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE zone_hist (
    id BIGINT NOT NULL,
    zone_id VARCHAR(100) NOT NULL,
    worker_id VARCHAR(100) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    exist_flag INT COMMENT '1 : 있음\n0 : 떠남',
    PRIMARY KEY (id)
);

CREATE TABLE equip_info (
    equip_id VARCHAR(100) NOT NULL,
    equip_name VARCHAR(255),
    zone_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (equip_id)
);

CREATE TABLE sensor_info (
    sensor_id VARCHAR(100) NOT NULL,
    sensor_type VARCHAR(255),
    val_unit VARCHAR(10),
    sensor_thres INT,
    created_at TIMESTAMP,
    zone_id VARCHAR(100) NOT NULL,
    equip_id VARCHAR(100) NOT NULL,
    iszone INT COMMENT '1: 공간에 대한 센서\n0 : 설비에 대한 센서',
    PRIMARY KEY (sensor_id)
);

CREATE TABLE abn_log (
    id BIGINT NOT NULL,
    target_type VARCHAR(50) COMMENT 'Sensor(공간- 012 rule-base), Worker, Equip(설비-머신러닝) 구분',
    target_id VARCHAR(100) COMMENT '센서 ID, 작업자 ID, 설비 ID 중 하나',
    abnormal_type VARCHAR(100) COMMENT '이상 유형 (예: 심박수 이상, 온도 초과, 진동 이상 등)',
    abn_val DOUBLE,
    detected_at TIMESTAMP COMMENT 'WorkerStatus.detected_at\nSensor.timestamp\nEquipAnom.base_date (? predict_date)',
    zone_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE notify_log (
    abnomal_id BIGINT NOT NULL,
    recipient_id VARCHAR(100) NOT NULL,
    notify_type VARCHAR(50) COMMENT 'Email, SMS, Push 등',
    notified_at TIMESTAMP,
    PRIMARY KEY (abnomal_id),
    CONSTRAINT FK_abn_log_TO_notify_log_1 FOREIGN KEY (abnomal_id) REFERENCES abn_log (id)
);

CREATE TABLE control_log (
    abnormal_id BIGINT NOT NULL,
    zone_id VARCHAR(100) NOT NULL,
    control_type VARCHAR(50) COMMENT '습도 -> 제습기, 온도 -> 에어컨, 미세먼지 -> 공기청정기 등등',
    control_val INT COMMENT '(예: "22도" -> 22, "습도 50%" -> 50)',
    control_stat INT COMMENT '1 : 성공\n0 : 실패',
    executed_at TIMESTAMP,
    PRIMARY KEY (abnormal_id),
    CONSTRAINT FK_abn_log_TO_control_log_1 FOREIGN KEY (abnormal_id) REFERENCES abn_log (id)
);
