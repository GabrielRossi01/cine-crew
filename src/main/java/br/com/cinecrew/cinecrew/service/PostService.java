package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.CreatePostRequest;
import br.com.cinecrew.cinecrew.dto.response.PostResponse;
import br.com.cinecrew.cinecrew.exception.ForbiddenOperationException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.PostMapper;
import br.com.cinecrew.cinecrew.model.Event;
import br.com.cinecrew.cinecrew.model.Post;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.repository.EventRepository;
import br.com.cinecrew.cinecrew.repository.PostRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ClubService clubService;
    private final PostMapper postMapper;

    @Transactional
    public PostResponse createPost(Long authenticatedUserId, Long eventId, CreatePostRequest request) {
        Event event = findEventById(eventId);

        clubService.assertMember(event.getClub().getId(), authenticatedUserId);

        User author = findUserById(authenticatedUserId);

        Post post = Post.builder()
                .event(event)
                .author(author)
                .imageUrl(request.imageUrl().trim())
                .caption(normalizeCaption(request.caption()))
                .build();

        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getClubFeed(Long authenticatedUserId, Long clubId, Pageable pageable) {
        clubService.assertMember(clubId, authenticatedUserId);

        return postRepository.findFeedByClubId(clubId, pageable)
                .map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long authenticatedUserId, Long postId) {
        Post post = findPostById(postId);

        clubService.assertMember(
                post.getEvent().getClub().getId(),
                authenticatedUserId
        );

        return postMapper.toResponse(post);
    }

    @Transactional
    public void deletePost(Long authenticatedUserId, Long postId) {
        Post post = findPostById(postId);

        Long clubId = post.getEvent().getClub().getId();

        boolean isAuthor = post.getAuthor().getId().equals(authenticatedUserId);
        boolean isAdmin = clubService.isAdmin(clubId, authenticatedUserId);

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenOperationException("Apenas o autor ou um administrador pode remover esta memória");
        }

        postRepository.delete(post);
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userId));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
    }

    private String normalizeCaption(String caption) {
        if (caption == null || caption.isBlank()) {
            return null;
        }

        return caption.trim();
    }
}