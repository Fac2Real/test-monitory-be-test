package com.factoreal.backend.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum RiskLevel {
    WARN(1),    // 주의 단계
    DANGER(2);    // 위험 단계
    private final int priority;
    RiskLevel(int priority) {
        this.priority = priority;
    }

    public static List<RiskLevel> getUpTo(RiskLevel level){
        return Arrays.stream(values())
                .filter(risklevel -> risklevel.priority <= level.getPriority())
                .sorted(Comparator.comparingInt(RiskLevel::getPriority))
                .collect(Collectors.toList());
    }

    public static RiskLevel fromString(String levelStr){
        return RiskLevel.valueOf(levelStr.toUpperCase());
    }
}
