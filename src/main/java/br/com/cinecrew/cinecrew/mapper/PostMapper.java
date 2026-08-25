package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.PostResponse;
import br.com.cinecrew.cinecrew.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final UserMapper userMapper;

    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }

        return new PostResponse(
                post.getId(),
                post.getEvent().getId(),
                userMapper.toSummaryResponse(post.getAuthor()),
                post.getImageUrl(),
                post.getCaption(),
                post.getCreatedAt()
        );
    }
}