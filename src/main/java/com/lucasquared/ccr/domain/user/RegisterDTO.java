package com.lucasquared.ccr.domain.user;

public record RegisterDTO(String name, String login, String password, UserRole role) {
}
