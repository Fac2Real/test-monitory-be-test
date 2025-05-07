-- ========================
-- V2__insert_initial_data.sql
-- 기본 데이터 삽입 (zone_info, role_info, equip_info, worker_info)
-- ========================

-- 1. zone_info
INSERT INTO zone_info (zone_id, zone_name) VALUES
('zone_001', '생산 라인 A'),
('zone_002', '포장 구역 B'),
('zone_003', '품질 검사 C');

-- 2. role_info
INSERT INTO role_info (role_id, zone_id) VALUES
('manager', 'zone_001'),
('operator', 'zone_002'),
('inspector', 'zone_003');

-- 3. equip_info
-- '작업장 센서' == NULL
INSERT INTO equip_info (equip_id, equip_name, zone_id) VALUES
('equip_001', '로봇 암 1호기', 'zone_001'),
('equip_000', '작업장 센서', 'zone_001'),
('equip_003', '자동 포장기 2호기', 'zone_002'),
('equip_002', '작업장 센서', 'zone_002'),
('equip_004', 'X-ray 검사기', 'zone_003'),
('equip_005', '작업장 센서', 'zone_003');

-- 4. worker_info
INSERT INTO worker_info (worker_id, name, phone_number, email, role_id, zone_id) VALUES
('worker_001', '홍길동', '010-1234-5678', 'hong@example.com', 'manager', 'zone_001'),
('worker_002', '김철수', '010-2345-6789', 'kim@example.com', 'operator', 'zone_002'),
('worker_003', '이영희', '010-3456-7890', 'lee@example.com', 'inspector', 'zone_003');
