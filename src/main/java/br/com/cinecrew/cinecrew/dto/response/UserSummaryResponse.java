package br.com.cinecrew.cinecrew.dto.response;

public record UserSummaryResponse(
        Long id,
        String name,
        String avatarUrl
) {}