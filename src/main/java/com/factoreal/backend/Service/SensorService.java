package com.factoreal.backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.factoreal.backend.Dto.SensorDto;
import com.factoreal.backend.Repository.SensorRepository;

@Service
public class SensorService {
    private final SensorRepository repo;

    public SensorService(SensorRepository repo) {
        this.repo = repo;
    }
    
    public List<SensorDto> getAllSensors() {
        return repo.findAll().stream()
        .map(s -> new SensorDto(s.getSensorId(), s.getSensorType()))
        .collect(Collectors.toList());
    }
}
