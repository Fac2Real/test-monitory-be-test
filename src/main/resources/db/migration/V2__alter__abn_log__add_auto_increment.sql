-- 1. 외래 키 제약 조건 삭제
ALTER TABLE notify_log DROP FOREIGN KEY FK_notify_log_to_abn_log;
ALTER TABLE control_log DROP FOREIGN KEY FK_control_log_to_abn_log;

-- 2. abn_log.id 컬럼을 AUTO_INCREMENT로 변경
ALTER TABLE abn_log MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

-- 3. 외래 키 제약 조건 다시 추가
ALTER TABLE notify_log
    ADD CONSTRAINT FK_notify_log_to_abn_log
        FOREIGN KEY (abnormal_id) REFERENCES abn_log (id);

ALTER TABLE control_log
    ADD CONSTRAINT FK_control_log_to_abn_log
        FOREIGN KEY (abnormal_id) REFERENCES abn_log (id);
