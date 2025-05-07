package com.factoreal.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// 센서 정보 Entity
@Entity
@Table(name = "sensor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  Sensor {

    @Id
    @Column(name = "sensor_id", length = 100, nullable = false, unique = true) // 센서ID
    private String sensorId;

    @Column(name = "sensor_type", length = 255) // 센서종류
    private String sensorType;

    @Column(name = "val_unit", length = 10)
    private String valUnit;

    @Column(name = "sensor_thres")
    private Integer sensorThres;  // 임계치

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 센서생성시간

    @Column(name = "iszone")
    private Integer isZone;  // 1: 공간용 센서 / 0: 설비용 센서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone; // 공간 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equip_id", nullable = false)
    private Equip equip; // 설비 고유 ID



    // To Do 아래 삭제 예정
    @Column(name = "sensor_status") // 센서상태
    private String sensorStatus;

    @Column(name = "sensor_purpose") // 센서목적
    private String sensorPurpose;

    @Column(name = "location")   // 위치
    private String location;

    @Column(name = "threshold") // 임계치
    private Integer threshold;

    @Column(name = "registered", nullable = false)
    private boolean registered;

}