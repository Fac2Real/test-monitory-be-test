-- ========================
-- V1_2__update_init_data__zone_equip_id.sql
-- zone_id 및 equip_id 수정 스크립트
-- ========================

-- 1. 외래키 제약 조건 제거
ALTER TABLE worker_info DROP FOREIGN KEY FK_role_info_TO_worker_info;
ALTER TABLE role_info DROP FOREIGN KEY FK_zone_info_TO_role_info_1;

-- 2. 종속 관계 있는 zone_id 먼저 업데이트 (부모 → 자식 순)
UPDATE zone_info SET zone_id = '20250507165750-827' WHERE zone_id = 'zone_001';
UPDATE zone_info SET zone_id = '20250507171046-862' WHERE zone_id = 'zone_002';
UPDATE zone_info SET zone_id = '20250507191546-243' WHERE zone_id = 'zone_003';

-- role_info 테이블의 zone_id도 업데이트
UPDATE role_info SET zone_id = '20250507165750-827' WHERE zone_id = 'zone_001';
UPDATE role_info SET zone_id = '20250507171046-862' WHERE zone_id = 'zone_002';
UPDATE role_info SET zone_id = '20250507191546-243' WHERE zone_id = 'zone_003';

-- worker_info 테이블의 zone_id도 업데이트
UPDATE worker_info SET zone_id = '20250507165750-827' WHERE zone_id = 'zone_001';
UPDATE worker_info SET zone_id = '20250507171046-862' WHERE zone_id = 'zone_002';
UPDATE worker_info SET zone_id = '20250507191546-243' WHERE zone_id = 'zone_003';

-- 3. equip_info 테이블의 equip_id 업데이트
UPDATE equip_info SET equip_id = '20250507171316-389' WHERE equip_id = 'equip_001';
UPDATE equip_info SET equip_id = '20250507165924-184' WHERE equip_id = 'equip_000';
UPDATE equip_info SET equip_id = '20250507181319-532' WHERE equip_id = 'equip_002';
UPDATE equip_info SET equip_id = '20250507190000-001' WHERE equip_id = 'equip_003';
UPDATE equip_info SET equip_id = '20250507190000-002' WHERE equip_id = 'equip_004';
UPDATE equip_info SET equip_id = '20250507190000-003' WHERE equip_id = 'equip_005';

-- sensor_info에 equip_id가 연결되어 있을 경우 함께 업데이트 필요
-- (필요 시 주석 해제)
-- UPDATE sensor_info SET equip_id = '20250507171316-389' WHERE equip_id = 'equip_001';
-- UPDATE sensor_info SET equip_id = '20250507165924-184' WHERE equip_id = 'equip_000';
-- UPDATE sensor_info SET equip_id = '20250507181319-532' WHERE equip_id = 'equip_002';
-- UPDATE sensor_info SET equip_id = '20250507190000-001' WHERE equip_id = 'equip_003';
-- UPDATE sensor_info SET equip_id = '20250507190000-002' WHERE equip_id = 'equip_004';
-- UPDATE sensor_info SET equip_id = '20250507190000-003' WHERE equip_id = 'equip_005';

-- 4. 외래키 제약 조건 복구
ALTER TABLE role_info
    ADD CONSTRAINT FK_zone_info_TO_role_info_1
        FOREIGN KEY (zone_id) REFERENCES zone_info (zone_id);

ALTER TABLE worker_info
    ADD CONSTRAINT FK_role_info_TO_worker_info
        FOREIGN KEY (role_id, zone_id) REFERENCES role_info (role_id, zone_id);
