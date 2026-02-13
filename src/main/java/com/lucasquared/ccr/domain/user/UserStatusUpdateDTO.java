package com.lucasquared.ccr.domain.user;

import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateDTO(
        @NotNull(message = "Active is required") Boolean active) {
}
