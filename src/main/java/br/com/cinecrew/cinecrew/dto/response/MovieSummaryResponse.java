package br.com.cinecrew.cinecrew.dto.response;

public record MovieSummaryResponse(
        Long id,
        String title,
        String posterUrl,
        Short releaseYear
) {}
