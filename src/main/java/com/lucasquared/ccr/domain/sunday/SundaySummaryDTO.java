package com.lucasquared.ccr.domain.sunday;

import com.lucasquared.ccr.domain.user.UserSummaryDTO;

import java.util.List;

public record SundaySummaryDTO(
        String date,
        SundayShift shift,
        List<UserSummaryDTO> users,
        int remainingSlots) {
}
