package com.factoreal.backend.strategy.enums;

import lombok.Getter;

@Getter
public enum AlarmType {
    HIGH_TEMP("온도 이상", "온도가 기준치를 초과했습니다."),
    HIGH_DUST("먼지 농도 이상", "먼지 농도가 기준치를 초과했습니다."),
    HIGH_VIBRATION("진동 감지", "진동이 감지되었습니다."),
    LOW_HUMIDITY("습도 낮음", "습도가 기준치 이하입니다."),
    VOC_DETECTED("유해가스 감지", "VOC(휘발성 유기화합물)가 감지되었습니다."),
    OVER_CURRENT("전류 초과", "기기의 전류 사용량이 비정상적입니다.");

    private final String title;
    private final String message;

    AlarmType(String title, String message) {
        this.title = title;
        this.message = message;
    }

}
