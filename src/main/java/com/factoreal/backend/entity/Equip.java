package com.factoreal.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equip_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 설비정보 Table
public class Equip {
    @Id
    @Column(name = "equip_id", nullable = false, unique = true)
    private String equipId;  // 설비ID

    @Column(name = "equip_name", nullable = false)
    private String equipName; // 설비명

    @Column(name = "zone_id", nullable = false)
    private String zoneId;
    
    // Foreign Key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "zone_id", insertable = false, updatable = false)
    private Zone zone;
}
