package com.lucasquared.ccr.domain.sunday;

import com.lucasquared.ccr.domain.attendance.ChildAttendanceResponseDTO;
import com.lucasquared.ccr.domain.user.UserSummaryDTO;

import java.util.List;

public record SundayReportDTO(
        String date,
        SundayShift shift,
        List<UserSummaryDTO> availableUsers,
        int remainingSlots,
        List<ChildAttendanceResponseDTO> attendances) {
}
