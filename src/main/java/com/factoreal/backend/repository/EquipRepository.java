package com.factoreal.backend.repository;

import java.util.List;
import java.util.Optional;

import com.factoreal.backend.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import com.factoreal.backend.entity.Equip;
import org.springframework.data.jpa.repository.Query;

public interface EquipRepository extends JpaRepository<Equip, String> {
    List<Equip> findByZoneZoneId(String zoneId);

    Optional<Equip> findByEquipId(String equipId);

    @Query("SELECT e FROM Equip e WHERE e.equipName = :equipName AND e.zone = :zone")
    Equip findByEquipNameAndZoneId(String equipName, Zone zone);
}