package br.com.cinecrew.cinecrew.repository;

import br.com.cinecrew.cinecrew.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByEventId(Long eventId);

    @Query("""
        SELECT p FROM Post p
        WHERE p.event.club.id = :clubId
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findFeedByClubId(
            @Param("clubId") Long clubId,
            Pageable pageable
    );
}
