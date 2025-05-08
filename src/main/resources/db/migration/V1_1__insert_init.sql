-- ========================
-- V2__insert_initial_data.sql
-- 기본 데이터 삽입 (zone_info, role_info, equip_info, worker_info)
-- ========================

-- 1. zone_info
INSERT INTO zone_info (zone_id, zone_name) VALUES
('20250507165750-827', '생산 라인 A'),
('20250507171046-862', '포장 구역 B'),
('20250507191546-243', '품질 검사 C');


-- 2. equip_info
-- '작업장 센서' == NULL
INSERT INTO equip_info (equip_id, equip_name, zone_id) VALUES
('20250507171316-389', '로봇 암 1호기', '20250507165750-827'),
('20250507165750-827', 'empty', '20250507165750-827'),
('20250507171316-340', '자동 포장기 2호기', '20250507171046-862'),
('20250507171046-862', 'empty', '20250507171046-862'),
('20250507171316-341', 'X-ray 검사기', '20250507191546-243'),
('20250507191546-243', 'empty', '20250507191546-243');


-- 3. sensor_info
-- sensor_info data
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10H-HUM-24060890', 'humid', null, null, null, '20250507171046-862', '20250507171316-340', 0);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10H-HUM-24060891', 'humid', null, null, null, '20250507171046-862', '20250507171046-862', 1);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10H-HUM-24060892', 'humid', null, null, null, '20250507165750-827', '20250507171046-827', 1);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10T-TEM-24060890', 'temp', null, null, null, '20250507165750-827', '20250507171316-389', 0);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10T-TEM-24060891', 'temp', null, null, null, '20250507165750-827', '20250507165750-827', 1);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060890', 'vibration', null, null, null, '20250507165750-827', '20250507171316-389', 0);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060891', 'vibration', null, null, null, '20250507165750-827', '20250507171316-389', 0);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060892', 'vibration', null, null, null, '20250507165750-827', '20250507171316-389', 0);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060893', 'vibration', null, null, null, '20250507165750-827', '20250507171316-389', 0);

