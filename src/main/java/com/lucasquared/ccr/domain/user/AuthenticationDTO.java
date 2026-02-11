package com.lucasquared.ccr.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationDTO(
        @NotBlank(message = "Login is required") @Size(max = 120, message = "Login must be at most 120 characters") String login,
        @NotBlank(message = "Password is required") @Size(max = 120, message = "Password must be at most 120 characters") String password) {
}
