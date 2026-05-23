package com.job.dto;

import com.job.model.Role;

public class AuthDtos {
    public record RegisterRequest(String name, String email, String password, Role role) {}
    public record LoginRequest(String email, String password) {}
    public record AuthResponse(String token, UserResponse user) {}
    public record UserResponse(Long id, String name, String email, Role role) {}
}
