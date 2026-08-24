package br.com.cinecrew.cinecrew.dto.response;

public record RankingEntryResponse(
        int position,
        Long userId,
        String name,
        String avatarUrl,
        int clubScore
) {}