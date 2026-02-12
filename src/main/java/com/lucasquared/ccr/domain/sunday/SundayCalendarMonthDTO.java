package com.lucasquared.ccr.domain.sunday;

import java.util.List;

public record SundayCalendarMonthDTO(
        String monthYear,
        List<SundayCalendarDayDTO> sundays) {
}
