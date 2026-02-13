package com.lucasquared.ccr.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDTO(
        @NotBlank(message = "Current password is required") @Size(min = 6, max = 120, message = "Current password must be between 6 and 120 characters") String currentPassword,
        @NotBlank(message = "New password is required") @Size(min = 6, max = 120, message = "New password must be between 6 and 120 characters") String newPassword) {
}
