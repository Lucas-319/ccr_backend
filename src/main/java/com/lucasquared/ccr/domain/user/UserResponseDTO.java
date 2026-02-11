package com.lucasquared.ccr.domain.user;

public record UserResponseDTO(String id, String name, String login, UserRole role) {
}
