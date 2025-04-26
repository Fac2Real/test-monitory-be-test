package com.factoreal.backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.factoreal.backend.Entity.Sensor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.factoreal.backend.Dto.SensorDto;
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

    // 전체 센서 리스트 조회 ( BE -> FE )
    @GetMapping
    public ResponseEntity<List<SensorDto>> list() {
        return ResponseEntity.ok(service.getAllSensors());
    }
}