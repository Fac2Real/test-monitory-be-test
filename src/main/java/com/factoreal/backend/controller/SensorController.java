package com.factoreal.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.factoreal.backend.Dto.SensorDto;
import com.factoreal.backend.Service.SensorService;

@RestController
@RequestMapping("/api/sensors") 
public class SensorController {
    
    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SensorDto>> list() {
        return ResponseEntity.ok(service.getAllSensors());
    }
}