package com.lucasquared.ccr.domain.child;

import com.lucasquared.ccr.domain.user.UserSummaryDTO;

import java.time.Instant;

public record ChildResponseDTO(
        String id,
        String name,
        String responsibleName,
        String responsibleContact,
        String allergies,
        UserSummaryDTO createdBy,
        UserSummaryDTO updatedBy,
        Instant createdAt,
        Instant updatedAt) {
}
