package com.factoreal.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "zone_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 공간정보 매핑 Table
public class Zone {
    @Id
    @Column(name = "zone_id", nullable = false, unique = true)
    private String zoneId; // 공간ID

    @Column(name = "zone_name", nullable = false)
    private String zoneName; // 공간명
    
}