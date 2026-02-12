package com.lucasquared.ccr.domain.sunday;

import java.util.List;

public record SundayCalendarDayDTO(
        String date,
        List<SundayReportDTO> reports) {
}
