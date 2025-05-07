package com.factoreal.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 설비 생성 요청 DTO ( FE -> BE )
public class EquipCreateRequest {
    private String equipName;
    private String zoneName; // 사용자가 선택한 공간명
}