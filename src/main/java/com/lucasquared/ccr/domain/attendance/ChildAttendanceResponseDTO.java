package com.lucasquared.ccr.domain.attendance;

import com.lucasquared.ccr.domain.child.ChildSummaryDTO;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import com.lucasquared.ccr.domain.user.UserSummaryDTO;

import java.time.Instant;

public record ChildAttendanceResponseDTO(
        String date,
        SundayShift shift,
        Boolean present,
        ChildSummaryDTO child,
        UserSummaryDTO markedBy,
        UserSummaryDTO updatedBy,
        Instant createdAt,
        Instant updatedAt) {
}
