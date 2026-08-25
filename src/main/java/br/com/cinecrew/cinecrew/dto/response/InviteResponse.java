package br.com.cinecrew.cinecrew.dto.response;

import java.time.Instant;

public record InviteResponse(
        String inviteCode,
        String inviteUrl,
        Instant expiresAt
) {}
