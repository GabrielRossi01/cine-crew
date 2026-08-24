package br.com.cinecrew.cinecrew.dto.response;

import br.com.cinecrew.cinecrew.model.enums.ClubMemberRole;

public record ClubMemberResponse(
        Long membershipId,
        UserSummaryResponse user,
        ClubMemberRole role,
        int clubScore
) {}