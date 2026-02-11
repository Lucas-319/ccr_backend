package com.lucasquared.ccr.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @NotBlank(message = "Name is required") @Size(max = 120, message = "Name must be at most 120 characters") String name,
        @NotBlank(message = "Login is required") @Size(max = 120, message = "Login must be at most 120 characters") String login,
        @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters") String password,
        @NotNull(message = "Role is required") UserRole role) {
}
