-- ========================
-- V1_3__update_init_sensor_data.sql
-- sensor_info data 추가
-- ========================

INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10H-HUM-24060890', 'humid', null, null, null, '20250507171046-862', '20250507190000-001', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10H-HUM-24060891', 'humid', null, null, null, '20250507171046-862', 'equip_000', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10H-HUM-24060892', 'humid', null, null, null, '20250507165750-827', 'equip_000', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10T-TEM-24060890', 'temp', null, null, null, '20250507165750-827', '20250507171316-389', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10T-TEM-24060891', 'temp', null, null, null, '20250507165750-827', 'equip_000', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060890', 'vibration', null, null, null, '20250507165750-827', '20250507190000-002', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060891', 'vibration', null, null, null, '20250507165750-827', '20250507190000-002', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060892', 'vibration', null, null, null, '20250507165750-827', '20250507190000-002', null);
INSERT INTO my_database.sensor_info (sensor_id, sensor_type, val_unit, sensor_thres, created_at, zone_id, equip_id, iszone) VALUES ('UA10V-VIB-24060893', 'vibration', null, null, null, '20250507165750-827', '20250507190000-002', null);
