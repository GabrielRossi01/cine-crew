package br.com.cinecrew.cinecrew.dto.response;

public record MovieResponse(
        Long id,
        Long tmdbId,
        String title,
        String posterUrl,
        Short releaseYear
) {}
