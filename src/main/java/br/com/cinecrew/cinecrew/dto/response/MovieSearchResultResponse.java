package br.com.cinecrew.cinecrew.dto.response;

public record MovieSearchResultResponse(
        Long tmdbId,
        String title,
        String posterUrl,
        String releaseDate
) {}
