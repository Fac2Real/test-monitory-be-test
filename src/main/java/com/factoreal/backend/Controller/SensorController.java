package com.factoreal.backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.factoreal.backend.Entity.Sensor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.factoreal.backend.Dto.SensorDto;
import com.factoreal.backend.Dto.SensorUpdateDto;
import com.factoreal.backend.Service.SensorService;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    // 센서 등록 (임시 코드)
    @PostMapping
    public ResponseEntity<Map<String, Boolean>> createSensor(@RequestBody SensorDto dto) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            Sensor sens = service.saveSensor(dto);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 전체 센서 리스트 조회 ( BE -> FE 센서ID, 센서종류 넘기기 )
    @GetMapping
    public ResponseEntity<List<SensorDto>> list() {
        return ResponseEntity.ok(service.getAllSensors());
    }

    // DB Sensor Table 업데이트 ( FE -> BE 센서ID 매핑해서 센서목적, 위치, 임계치 업데이트 )
    @PostMapping("/{sensorId}")
    public ResponseEntity<Void> update(
            @PathVariable("sensorId") String sensorId,
            @RequestBody SensorUpdateDto dto) {
        service.updateSensor(sensorId, dto);
        return ResponseEntity.noContent().build();
    }

    // 미등록 센서 리스트 조회 ( BE -> FE )
    @GetMapping("/unregistered")
    public ResponseEntity<List<SensorDto>> getUnregisteredSensors() {
        List<SensorDto> sensors = service.getUnregisteredSensors();
        return ResponseEntity.ok(sensors);
    }
}