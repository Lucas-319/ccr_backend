package com.lucasquared.ccr.domain.attendance;

import com.lucasquared.ccr.domain.sunday.SundayShift;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ChildAttendanceUpdateDTO(
        @NotBlank(message = "Date is required") @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Date must be dd/MM/yyyy") String date,
        @NotNull(message = "Shift is required") SundayShift shift,
        @NotNull(message = "Present is required") Boolean present) {
}
