package com.lucasquared.ccr.domain.sunday;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SundayCreateDTO(
        @NotBlank(message = "Date is required") @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Date must be dd/MM/yyyy") String date,
        @NotNull(message = "Shift is required") SundayShift shift) {
}
