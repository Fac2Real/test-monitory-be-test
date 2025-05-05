package com.factoreal.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.RoleInfo;

@Entity
@Data
@Table(name = "worker_info")
@NoArgsConstructor
public class Worker {
    @Id
    @Column(name = "worker_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String workerId;

    @Column(name = "worker_name")
    @Size(min = 2, max = 100)
    @NotNull
    private String name;

    @Column(name = "phone_number")
    @Pattern(
            regexp = "^\\+\\d{1,15}$",
            message = "전화번호는 +부터 시작하는 국제전화(E.164) 형식이어야 합니다."
    )
    @NotNull
    private String phoneNumber; // 예 ) +821012345678

    @Column(name = "email")
    @NotNull
    @Email
    private String email;

}
