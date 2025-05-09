package com.factoreal.backend.repository;

import com.factoreal.backend.entity.AbnormalLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbnLogRepository extends JpaRepository<AbnormalLog,Long> {
}
