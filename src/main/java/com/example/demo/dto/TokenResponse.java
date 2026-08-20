package com.example.demo.dto;

public record TokenResponse(
        String token,
        String type,
        long expiresIn
) {
}
