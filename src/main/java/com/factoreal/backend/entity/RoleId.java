package com.factoreal.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
// role_info 테이블 복합키 정의
public class RoleId implements Serializable {

    @Column(name = "role_id", length = 50, nullable = false)
    private String roleId;

    @Column(name = "zone_id", length = 100, nullable = false)
    private String zoneId;
}
