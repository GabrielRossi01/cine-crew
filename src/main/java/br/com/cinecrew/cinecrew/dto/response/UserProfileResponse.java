package br.com.cinecrew.cinecrew.dto.response;

public record UserProfileResponse(
        Long id,
        String name,
        String username,
        String email,
        String avatarUrl
) {}