package com.factoreal.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.factoreal.backend.entity.Equip;
import com.factoreal.backend.entity.Zone;
import com.factoreal.backend.repository.EquipRepository;
import com.factoreal.backend.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.factoreal.backend.dto.SensorDto;
import com.factoreal.backend.dto.SensorUpdateDto;
import com.factoreal.backend.entity.Sensor;
import com.factoreal.backend.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository repo;
    private final ZoneRepository zoneRepo;
    private final EquipRepository equipRepo;

    // 센서 등록
    public Sensor saveSensor(SensorDto dto) {
        Optional<Zone> zoneOptional = zoneRepo.findById(dto.getZoneId());
        if(zoneOptional.isEmpty()) {
            log.error("Zone not found");
            return null;
        }
        Optional<Equip> equipOptional = equipRepo.findById(dto.getEquipId());
        if(equipOptional.isEmpty()) {
            log.error("Equip not found");
            return null;
        }
        Sensor sens = new Sensor();
        sens.setSensorId(dto.getSensorId());
        sens.setSensorType(dto.getSensorType());
        sens.setZone(zoneOptional.get());
        sens.setEquip(equipOptional.get());
        return repo.save(sens);
    }

    // 센서 전체 리스트 조회
    public List<SensorDto> getAllSensors() {
        return repo.findAll().stream()
        .map(s -> new SensorDto(s.getSensorId(), s.getSensorType(),s.getZone().getZoneId(),s.getEquip().getEquipId()))
        .collect(Collectors.toList());
    }

    // Sensor Table 업데이트
    @Transactional
    public void updateSensor(String sensorId, SensorUpdateDto dto) {
        Sensor sensor = repo.findBySensorId(sensorId)
            .orElseThrow(() -> new RuntimeException("SensorID = "+sensorId+" 센서를 찾을 수 없습니다."));
//        sensor.setSensorPurpose(dto.getSensorPurpose());
//        sensor.setLocation(dto.getLocation());
//        sensor.setThreshold(dto.getThreshold());
        repo.save(sensor);
    }
}