package com.factoreal.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 설비 정보 DTO
public class EquipDto {
    private String equipId;
    private String equipName;
    private String zoneName;
    private String zoneId;
}