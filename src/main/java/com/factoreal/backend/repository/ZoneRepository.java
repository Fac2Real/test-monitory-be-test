package com.factoreal.backend.repository;

import com.factoreal.backend.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, String> {
    Optional<Zone> findByZoneName(String zoneName);
    Zone findByZoneId(String zoneName);
}
