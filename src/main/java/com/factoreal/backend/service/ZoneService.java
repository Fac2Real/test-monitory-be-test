package com.factoreal.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.factoreal.backend.dto.EquipDto;
import com.factoreal.backend.entity.Equip;
import com.factoreal.backend.repository.EquipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.factoreal.backend.dto.ZoneDto;
import com.factoreal.backend.dto.ZoneUpdateDto;
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
    private final EquipService equipService;
    // 1. 공간명 중복 체크 -> 2. 고유한 공간ID 할당 -> 3. save 한 뒤 DTO로 반환
    @Transactional
    public ZoneDto createZone(String zoneName) {
        // 1. 공간명 중복 체크
        if (repo.findByZoneName(zoneName).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "이미 존재하는 공간명: " + zoneName);
        }
        // 2. 고유한 공간ID 할당
        String zoneId = ZoneIdGenerator.generateZoneId();
        // 3. save 한 뒤 DTO로 반환
        Zone zone = repo.save(new Zone(zoneId, zoneName));
        // 4. equip_id 가 없는 (설비 명이 없는 데이터를 위한 빈 equip_객체 생성)
        //equipRepo.
        EquipDto equipDto = EquipDto
                .builder()
                .equipName("empty")
                .zoneId(zoneId)
                .zoneName(zoneName)
                .build();
        equipService.saveEquip(equipDto);
        return new ZoneDto(zone.getZoneId(), zone.getZoneName());
    }

    // 공간정보 업데이트
    @Transactional
    public ZoneDto updateZone(String zoneId, ZoneUpdateDto dto) {
        // 1. 수정할 공간이 존재하는지 확인
        Zone zone = repo.findById(zoneId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "존재하지 않는 공간 ID: " + zoneId));

        // 2. 새로운 공간명이 이미 존재하는지 확인
        if (!zone.getZoneName().equals(dto.getZoneName()) && 
            repo.findByZoneName(dto.getZoneName()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "이미 존재하는 공간명: " + dto.getZoneName());
        }

        // 3. 공간명 업데이트
        zone.setZoneName(dto.getZoneName());
        Zone updatedZone = repo.save(zone);

        // 4. DTO로 반환
        return new ZoneDto(updatedZone.getZoneId(), updatedZone.getZoneName());
    }

    // 모든 공간 조회
    public List<ZoneDto> getAllZones() {
        return repo.findAll().stream()
                .map(zone -> new ZoneDto(zone.getZoneId(), zone.getZoneName()))
                .collect(Collectors.toList());
    }

    public Zone getZone(String zoneId) {
        return repo.findByZoneId(zoneId);
    }
}