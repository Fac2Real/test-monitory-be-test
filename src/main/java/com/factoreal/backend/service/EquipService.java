package com.factoreal.backend.service;

import com.factoreal.backend.dto.EquipCreateRequest;
import com.factoreal.backend.dto.EquipDto;
import com.factoreal.backend.dto.EquipUpdateDto;
import com.factoreal.backend.entity.Equip;
import com.factoreal.backend.entity.Zone;
import com.factoreal.backend.repository.EquipRepository;
import com.factoreal.backend.repository.ZoneRepository;
import com.factoreal.backend.util.EquipIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipService {
    private final EquipRepository equipRepo;
    private final ZoneRepository zoneRepo;

    // 설비 등록
    @Transactional
    public Equip saveEquip(EquipDto dto) {
        Zone zone = zoneRepo.findById(dto.getZoneId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 공간 ID: " + dto.getZoneId()));
        // 설비 정보 저장
        Equip equips = new Equip();
        equips.setEquipId(zone.getZoneId());
        equips.setEquipName(dto.getEquipName());
        equips.setZone(zone);
        return equipRepo.save(equips);
    }


    // 설비 생성
    @Transactional
    public EquipDto createEquip(EquipCreateRequest req) {
        // 1. UI에서 입력받은 zoneName으로 zoneId 조회
        Zone zone = zoneRepo.findByZoneName(req.getZoneName())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "존재하지 않는 공간명: " + req.getZoneName()));
    
        // 2. 고유한 설비ID 생성
        String equipId = EquipIdGenerator.generateEquipId();

        // 3. 설비 정보 저장
        Equip equip = new Equip(equipId, req.getEquipName(), zone);
        equipRepo.save(equip);

        // 4. DTO로 반환
        return new EquipDto(equipId, req.getEquipName(), zone.getZoneName(), zone.getZoneId());

    }

    // 설비 정보 수정
    @Transactional
    public EquipDto updateEquip(String equipId, EquipUpdateDto dto) {
        // 1. 수정할 설비가 존재하는지 확인
        Equip equip = equipRepo.findById(equipId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "존재하지 않는 설비 ID: " + equipId));

        // 2. 설비명 업데이트
        equip.setEquipName(dto.getEquipName());
        Equip updated = equipRepo.save(equip);

        // 3. DTO로 반환
        Zone zone = zoneRepo.findById(updated.getZone().getZoneId())
            .orElse(new Zone("", "미등록 공간"));

        return new EquipDto(
            updated.getEquipId(),
            updated.getEquipName(),
            zone.getZoneName(),
            zone.getZoneId()
        );

    }
    

    // 모든 설비 조회
    public List<EquipDto> getAllEquips() {
        return equipRepo.findAll().stream()
        .map(equip -> {
            Zone zone = zoneRepo.findById(equip.getZone().getZoneId())
                .orElse(new Zone("", "미등록 공간"));
            return new EquipDto(
                equip.getEquipId(),
                equip.getEquipName(), 
                zone.getZoneName(),
                equip.getZone().getZoneId()
            );
        })
        .collect(Collectors.toList());
    }
}
