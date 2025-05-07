package com.factoreal.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// role_info entity
public class Role {

    @EmbeddedId
    private RoleId id; // 복합키ID

    // zone_id 외래키 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", insertable = false, updatable = false)
    private Zone zone;
}
