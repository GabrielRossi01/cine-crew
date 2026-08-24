package br.com.cinecrew.cinecrew.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PostResponse(
        Long id,
        UUID eventId,
        UserSummaryResponse author,
        String imageUrl,
        String caption,
        Instant createdAt
) {}