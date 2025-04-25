package com.factoreal.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.factoreal.backend.Entity.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, String> {
}