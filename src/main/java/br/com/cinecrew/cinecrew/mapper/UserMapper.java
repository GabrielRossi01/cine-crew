package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.UserProfileResponse;
import br.com.cinecrew.cinecrew.dto.response.UserSummaryResponse;
import br.com.cinecrew.cinecrew.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummaryResponse toSummaryResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getAvatarUrl()
        );
    }

    public UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }
}
