package com.factoreal.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.factoreal.backend.entity.Equip;

public interface EquipRepository extends JpaRepository<Equip, String> {
    List<Equip> findByZoneId(String zoneId);
}