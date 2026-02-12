package com.lucasquared.ccr.domain.sunday;

import com.lucasquared.ccr.domain.user.UserSummaryDTO;

public record SundayAvailabilityResponseDTO(
        String date,
        SundayShift shift,
        UserSummaryDTO user) {
}
