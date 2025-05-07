package com.factoreal.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.factoreal.backend.entity.Sensor;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    Optional<Sensor> findBySensorId(String sensorId);

    // 미등록 센서 리스트 조회 ( BE -> FE )
    List<Sensor> findByRegisteredFalseAndSensorIdIsNotNull();
}