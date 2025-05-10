package com.factoreal.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.factoreal.backend.dto.*;
import com.factoreal.backend.entity.Equip;
import com.factoreal.backend.entity.Sensor;
import com.factoreal.backend.repository.EquipRepository;
import com.factoreal.backend.repository.SensorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.factoreal.backend.entity.Zone;
import com.factoreal.backend.repository.ZoneRepository;
import com.factoreal.backend.util.ZoneIdGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository repo;

    private final SensorRepository sensorRepo;
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
    public ZoneDto updateZone(String zoneName, ZoneUpdateDto dto) {
        // 1. 수정할 공간이 존재하는지 확인
        Zone zone = repo.findByZoneName(zoneName)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "존재하지 않는 공간: " + zoneName));

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

    @Transactional
    public List<ZoneItemDto> getZoneItems() {

        List<Zone> zones = repo.findAll();

        return zones.stream()
                .map(zone -> {

                    List<Sensor> sensors = sensorRepo.findByZone(zone);

                    // 환경 센서
                    List<Sensor> envSensors = sensors.stream()
                            .filter(s -> Objects.equals(s.getZone().getZoneId(), s.getEquip().getEquipId()))
                            .toList();

                    // 1) Sensor 엔티티 → SensorDto 변환
                    List<SensorDto> envSensorDtos = envSensors.stream()      // List<Sensor>
                            .map(SensorDto::fromEntity)                      // Sensor → SensorDto
                            .toList();


                    List<Equip> equips = equipRepo.findEquipsByZone(zone).stream()
                            .filter(e -> e.getEquipName() != null && !e.getEquipName().equalsIgnoreCase("empty"))
                            .toList();   // empty이름을 가진 설비(환경센서)는 설비 목록에서 제외하기

                    // 설비 센서 그룹핑
                    Map<String, List<SensorDto>> facGroup = sensors.stream()
                            .filter(s -> !Objects.equals(s.getZone().getZoneId(), s.getEquip().getEquipId()))
                            .map(SensorDto::fromEntity)                 // ★ Sensor → SensorDto
                            .collect(Collectors.groupingBy(SensorDto::getEquipId));

                    List<FacilityDto> facilities = equips.stream()
                            .map(entry -> {

                                String equipId = entry.getEquipId();
                                String equipName = equipRepo.findEquipNameByEquipId(equipId); // 1-row 조회

                                List<SensorDto> facSensors = facGroup.getOrDefault(equipId, List.of());

                                return FacilityDto.builder()
                                        .name(equipName)
                                        .facSensor(facSensors)
                                        .build();
                            })
                            .toList();

                    /* 4) ZoneItemDto 조립 */
                    return ZoneItemDto.builder()
                            .title(zone.getZoneName())
                            .envSensor(envSensorDtos)
                            .facility(facilities)
                            .build();
                })
                .toList();
    }

    public Zone getZone(String zoneId) {
        return repo.findByZoneId(zoneId);
    }
}