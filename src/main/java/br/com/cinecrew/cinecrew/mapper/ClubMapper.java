package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.ClubResponse;
import br.com.cinecrew.cinecrew.model.Club;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClubMapper {

    private final UserMapper userMapper;

    public ClubResponse toResponse(Club club) {
        if (club == null) {
            return null;
        }

        return new ClubResponse(
                club.getId(),
                club.getName(),
                club.getDescription(),
                userMapper.toSummaryResponse(club.getOwner()),
                club.getCreatedAt()
        );
    }
}
