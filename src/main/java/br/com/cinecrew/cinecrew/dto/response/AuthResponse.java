package br.com.cinecrew.cinecrew.dto.response;

public record AuthResponse(
        String token,
        long expiresInSeconds
) {}