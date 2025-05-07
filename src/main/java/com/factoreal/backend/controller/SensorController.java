package com.factoreal.backend.controller;

import com.factoreal.backend.dto.SensorDto;
import com.factoreal.backend.dto.SensorUpdateDto;
import com.factoreal.backend.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@Tag(name = "센서 정보 API", description = "센서 정보 처리 API입니다.")
public class SensorController {

    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    // // 센서 등록 (임시 코드)
    // @PostMapping
    // // @Operation(summary = "센서 등록 (임시 기능)", description = "센서 정보를 수동으로 등록하는 임시 메서드", hidden = true)
    // @Operation(summary = "센서 등록 (임시 기능)", description = "센서 정보를 수동으로 등록하는 임시 메서드", hidden = false)
    // public ResponseEntity<Map<String, Boolean>> createSensor(@RequestBody SensorDto dto) {
    //     Map<String, Boolean> response = new HashMap<>();
    //     try {
    //         Sensor sens = service.saveSensor(dto);
    //         response.put("success", true);
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         response.put("success", false);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    //     }
    // }

    // 전체 센서 리스트 조회 ( BE -> FE 센서ID, 센서종류 넘기기 )
    @GetMapping
    @Operation(summary = "전체 센서 리스트 조회", description = "전체 센서 정보를 조회하는 기능")
    public ResponseEntity<List<SensorDto>> list() {
        return ResponseEntity.ok(service.getAllSensors());
    }

    // DB Sensor Table 업데이트 ( FE -> BE 센서ID 매핑해서 센서목적, 위치, 임계치 업데이트 )
    @PostMapping("/{sensorId}")
    @Operation(summary = "센서 정보 업데이트", description = "센서ID 매핑해서 센서목적, 위치, 임계치 업데이트 (FE -> BE) ")
    public ResponseEntity<Void> update(
            @PathVariable("sensorId") String sensorId,
            @RequestBody SensorUpdateDto dto) {
        service.updateSensor(sensorId, dto);
        return ResponseEntity.noContent().build();
    }
}