package com.factoreal.backend.repository;

import java.util.Optional;

import com.factoreal.backend.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import com.factoreal.backend.entity.Sensor;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    Optional<Sensor> findBySensorId(String sensorId);
    List<Sensor> findByZone(Zone zone);
}