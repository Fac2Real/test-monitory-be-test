package com.factoreal.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.factoreal.backend.strategy.enums.SensorType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.factoreal.backend.dto.SensorDto;
import com.factoreal.backend.dto.SensorUpdateDto;
import com.factoreal.backend.entity.Equip;
import com.factoreal.backend.entity.Sensor;
import com.factoreal.backend.entity.Zone;
import com.factoreal.backend.repository.EquipRepository;
import com.factoreal.backend.repository.SensorRepository;
import com.factoreal.backend.repository.ZoneRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository repo;
    private final ZoneRepository zoneRepo;
    private final EquipRepository equipRepo;

    // 센서 등록
    @Transactional
    public Sensor saveSensor(SensorDto dto) {
        // 1. Zone 존재 여부 확인
        Zone zone = zoneRepo.findById(dto.getZoneId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "존재하지 않는 공간 ID: " + dto.getZoneId()));

        // 2. Equip 존재 여부 확인
        Optional<Equip> equip = Optional.ofNullable(equipRepo.findByEquipId(dto.getEquipId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 설비 ID: " + dto.getEquipId())));
        if(equip.isEmpty()) {
            log.info("존재하지 않는 설비 ID");

        }
        // 3. 센서 정보 저장
        Sensor sens = new Sensor();
        sens.setSensorId(dto.getSensorId());
        sens.setSensorType(SensorType.valueOf(dto.getSensorType()));
        sens.setZone(zone);
        sens.setEquip(equip.get());
        return repo.save(sens);
    }

    // 센서 전체 리스트 조회
    public List<SensorDto> getAllSensors() {
        return repo.findAll().stream()
        .map(s -> new SensorDto(s.getSensorId(), s.getSensorType().toString(), s.getZone().getZoneId(), s.getEquip().getEquipId()))
        .collect(Collectors.toList());
    }

    // Sensor Table 업데이트
    @Transactional
    public void updateSensor(String sensorId, SensorUpdateDto dto) {
        Sensor sensor = repo.findBySensorId(sensorId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "존재하지 않는 센서 ID: " + sensorId));
        repo.save(sensor);
    }
}