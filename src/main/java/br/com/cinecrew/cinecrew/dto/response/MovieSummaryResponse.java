package br.com.cinecrew.cinecrew.dto.response;

public record MovieSummaryResponse(
        Long id,
        Long tmdbId,
        String title,
        String posterUrl,
        Short releaseYear
) {}
