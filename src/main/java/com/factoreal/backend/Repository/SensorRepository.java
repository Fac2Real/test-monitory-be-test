package com.factoreal.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.factoreal.backend.Entity.Sensor;
import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    // 미등록 센서 리스트 조회 ( BE -> FE )
    List<Sensor> findByRegisteredFalseAndSensorIdIsNotNull();
}