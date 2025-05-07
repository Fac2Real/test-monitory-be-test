package com.factoreal.backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "worker_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 작업자 정보 Table
public class Worker {

    @Id
    @Column(name = "worker_id", length = 100, nullable = false)
    private String workerId; //작업자 고유 ID

    @Column(name = "name", length = 100)
    private String name; // 작업자 이름

    @Column(name = "phone_number", length = 50)
    private String phoneNumber; // 작업자 번호

    @Column(name = "email", length = 100)
    private String email; // 작업자 이메일

    @Column(name = "role_id", length = 50, nullable = false)
    private String roleId; //

    @Column(name = "zone_id", length = 100, nullable = false)
    private String zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false),
            @JoinColumn(name = "zone_id", referencedColumnName = "zone_id", insertable = false, updatable = false)
    })
    private RoleInfo roleInfo;
    
}