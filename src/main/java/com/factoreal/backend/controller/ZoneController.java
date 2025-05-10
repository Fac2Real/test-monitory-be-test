package com.factoreal.backend.controller;

import java.util.List;
import java.util.Map;

import com.factoreal.backend.dto.ZoneItemDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.factoreal.backend.dto.ZoneDto;
import com.factoreal.backend.dto.ZoneUpdateDto;
import com.factoreal.backend.service.ZoneService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@Tag(name = "공간 정보 API", description = "공간(Zone) 매핑 처리 API")
public class ZoneController {
    private final ZoneService service;
    
    @PostMapping
    @Operation(summary = "공간 생성", description = "UI에서 입력한 공간명으로 Zone을 등록하고 고유 zoneId를 생성하여 반환합니다.")
    public ResponseEntity<ZoneDto> createZone(@RequestBody Map<String, String> req) {
        // 1. 공간명 받아오기
        String zoneName = req.get("zoneName");
         // 2. service 호출
        ZoneDto created = service.createZone(zoneName);
        // 3. 201 CREATED 응답으로 DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{zoneName}")
    @Operation(summary = "공간 정보 수정", description = "기존 공간의 이름을 수정합니다.")
    public ResponseEntity<ZoneDto> updateZone(
            @PathVariable String zoneName,
            @RequestBody ZoneUpdateDto dto) {
        ZoneDto updated = service.updateZone(zoneName, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @Operation(summary = "공간 리스트 조회", description = "등록된 모든 공간 정보를 조회합니다.")
    public ResponseEntity<List<ZoneDto>> listZones() {
        List<ZoneDto> list = service.getAllZones();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/zoneitems")
    @Operation(summary = "공간별 설비,센서 데이터 조회", description = "등록된 공간들의 각 정보를 조회합니다.")
    public ResponseEntity<List<ZoneItemDto>> listZoneItems() {
        return ResponseEntity.ok(service.getZoneItems());
    }
}