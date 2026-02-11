package com.lucasquared.ccr.domain.child;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChildCreateDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be at most 120 characters")
        String name,
        @NotBlank(message = "Responsible name is required")
        @Size(max = 120, message = "Responsible name must be at most 120 characters")
        String responsibleName,
        @NotBlank(message = "Responsible contact is required")
        @Size(max = 60, message = "Responsible contact must be at most 60 characters")
        String responsibleContact,
        @Size(max = 255, message = "Allergies must be at most 255 characters")
        String allergies) {
}
