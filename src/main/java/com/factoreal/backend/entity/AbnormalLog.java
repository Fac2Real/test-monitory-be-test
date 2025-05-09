package com.factoreal.backend.entity;

import com.factoreal.backend.dto.LogType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // ID

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 50)
    private LogType targetType; // 구분 분류 :  Sensor(공간- 012 rule-base), Worker, Equip(설비-머신러닝) 구분

    @Column(name = "target_id", length = 100)
    private String targetId; // 고유 ID : 센서ID, WorkerID, EquipID

    @Column(name = "abnormal_type", length = 100)
    private String abnormalType; // 이상 유형 : (예: 심박수 이상, 온도 초과, 진동 이상 등)

    @Column(name = "abn_val")
    private Double abnVal; // 이상치 값

    @Column(name = "detected_at")
    @CreatedDate
    private LocalDateTime detectedAt; // 이상 감지 시간

    // FK: zone_id → zone_info
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "zone_id")
    private Zone zone; // 공간 고유 ID
}
