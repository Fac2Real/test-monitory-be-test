package com.factoreal.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "abn_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 이상 감지 LOG ENTITY
public class AbnormalLog {

    @Id
    @Column(name = "id", nullable = false)
    private Long id; // ID

    @Column(name = "target_type", length = 50)
    private String targetType; // 구분 분류

    @Column(name = "target_id", length = 100)
    private String targetId; // 고유 ID

    @Column(name = "abnormal_type", length = 100)
    private String abnormalType; // 이상 유형

    @Column(name = "abn_val")
    private Double abnVal; // 이상치 값

    @Column(name = "detected_at")
    private LocalDateTime detectedAt; // 이상 감지 시간

    // FK: zone_id → zone_info
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "zone_id")
    private Zone zone; // 공간 고유 ID
}
