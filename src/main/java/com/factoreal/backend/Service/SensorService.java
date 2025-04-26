package com.factoreal.backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.factoreal.backend.Entity.Sensor;
import org.springframework.stereotype.Service;

import com.factoreal.backend.Dto.SensorDto;
import com.factoreal.backend.Dto.SensorUpdateDto;
import com.factoreal.backend.Repository.SensorRepository;

@Service
public class SensorService {
    private final SensorRepository repo;

    public SensorService(SensorRepository repo) {
        this.repo = repo;
    }

    // 센서 등록
    public Sensor saveSensor(SensorDto dto) {
        Sensor sens = new Sensor();
        sens.setSensorId(dto.getSensorId());
        sens.setSensorType(dto.getSensorType());
        // sens.setSensorStatus(dto.getSensorStatus());
        return repo.save(sens);
    }

    // 센서 전체 리스트 조회
    public List<SensorDto> getAllSensors() {
        return repo.findAll().stream()
        .map(s -> new SensorDto(s.getSensorId(), s.getSensorType()))
        .collect(Collectors.toList());
    }

    // 미등록 센서 리스트 조회 ( BE -> FE )
    public List<SensorDto> getUnregisteredSensors() {
        return repo.findByRegisteredFalseAndSensorIdIsNotNull().stream()
                .filter(s -> !s.getSensorId().isBlank())
                .map(s -> new SensorDto(s.getSensorId(), s.getSensorType()))
                .collect(Collectors.toList());
    }

    // Sensor Table 업데이트
    public void updateSensor(String sensorId, SensorUpdateDto dto) {
        Sensor sensor = repo.findBySensorId(sensorId)
            .orElseThrow(() -> new RuntimeException("SensorID = "+sensorId+" 센서를 찾을 수 없습니다."));

        sensor.setSensorPurpose(dto.getSensorPurpose());
        sensor.setLocation(dto.getLocation());
        sensor.setThreshold(dto.getThreshold());
        repo.save(sensor);
    }
}
