package br.com.cinecrew.cinecrew.dto.response;

import java.time.Instant;

public record WatchlistItemResponse(
        Long id,
        MovieSummaryResponse movie,
        Instant addedAt
) {}
