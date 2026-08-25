package br.com.cinecrew.cinecrew.dto.response;

import java.time.Instant;

public record PostResponse(
        Long id,
        Long eventId,
        UserSummaryResponse author,
        String imageUrl,
        String caption,
        Instant createdAt
) {}