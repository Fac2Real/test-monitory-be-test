package com.factoreal.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.factoreal.backend.repository.EquipRepository;
import org.springframework.stereotype.Service;

import com.factoreal.backend.dto.ZoneDto;
import com.factoreal.backend.entity.Zone;
import com.factoreal.backend.repository.ZoneRepository;
import com.factoreal.backend.util.ZoneIdGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository repo;
    private final EquipRepository equipRepo;
    // 1. 공간명 중복 체크 -> 2. 고유한 공간ID 할당 -> 3. save 한 뒤 DTO로 반환
    @Transactional
    public ZoneDto createZone(String zoneName) {
        // 1. 공간명 중복 체크
        if (repo.findByZoneName(zoneName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 공간명: "+zoneName);
        }
        // 2. 고유한 공간ID 할당
        String zoneId = ZoneIdGenerator.generateZoneId(zoneName);
        // 3. save 한 뒤 DTO로 반환
        Zone zone = repo.save(new Zone(zoneId, zoneName));
        // 4. equip_id 가 없는 (설비 명이 없는 데이터를 위한 빈 equip_객체 생성)
        //equipRepo.

        return new ZoneDto(zone.getZoneId(), zone.getZoneName());
    }

    // 모든 공간 조회
    public List<ZoneDto> getAllZones() {
        return repo.findAll().stream()
            .map(zone -> new ZoneDto(zone.getZoneId(), zone.getZoneName()))
            .collect(Collectors.toList());
    }
}
