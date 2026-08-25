package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.ClubMemberResponse;
import br.com.cinecrew.cinecrew.dto.response.RankingEntryResponse;
import br.com.cinecrew.cinecrew.dto.response.UserSummaryResponse;
import br.com.cinecrew.cinecrew.model.ClubMember;
import org.springframework.stereotype.Component;

@Component
public class ClubMemberMapper {

    public ClubMemberResponse toResponse(ClubMember member) {
        if (member == null) {
            return null;
        }

        return new ClubMemberResponse(
                member.getId(),
                new UserSummaryResponse(
                        member.getUser().getId(),
                        member.getUser().getName(),
                        member.getUser().getAvatarUrl()
                ),
                member.getRole(),
                member.getClubScore()
        );
    }

    public RankingEntryResponse toRankingEntry(ClubMember member, int position) {
        if (member == null) {
            return null;
        }

        return new RankingEntryResponse(
                position,
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getAvatarUrl(),
                member.getClubScore()
        );
    }
}