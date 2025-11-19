package com.kuit.kupage.domain.teamMatch.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ApplyTimeConverter {

    public static String formatTimetable(LocalDateTime dateTime) {
        if (dateTime == null) {
            return ""; // null 처리
        }

        // 1. 포맷터 정의: "월/일 (요일) 시:분" 형식
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd (E) HH:mm", Locale.KOREAN);

        // 2. 변환 및 반환
        return dateTime.format(formatter);
    }
}