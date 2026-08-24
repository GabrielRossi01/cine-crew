package br.com.cinecrew.cinecrew.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ClubResponse(
        Long id,
        String name,
        String description,
        UserSummaryResponse owner,
        Instant createdAt
) {}