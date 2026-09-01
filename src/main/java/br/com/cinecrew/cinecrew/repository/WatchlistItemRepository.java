package br.com.cinecrew.cinecrew.repository;

import br.com.cinecrew.cinecrew.model.WatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    Page<WatchlistItem> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    Optional<WatchlistItem> findByUserIdAndMovieId(Long userId, Long movieId);
}
