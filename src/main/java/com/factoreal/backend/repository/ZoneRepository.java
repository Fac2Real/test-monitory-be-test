package com.factoreal.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.factoreal.backend.entity.Zone;
import org.springframework.data.jpa.repository.Query;

public interface ZoneRepository extends JpaRepository<Zone, String> {
    Optional<Zone> findByZoneName(String zoneName);
}
