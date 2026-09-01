package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.AddToWatchlistRequest;
import br.com.cinecrew.cinecrew.dto.response.WatchlistItemResponse;
import br.com.cinecrew.cinecrew.mapper.WatchlistItemMapper;
import br.com.cinecrew.cinecrew.model.Movie;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.WatchlistItem;
import br.com.cinecrew.cinecrew.repository.MovieRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import br.com.cinecrew.cinecrew.repository.WatchlistItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {

    private final WatchlistItemRepository watchlistItemRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final WatchlistItemMapper watchlistItemMapper;

    @Transactional
    public WatchlistItemResponse addToWatchlist(Long userId, AddToWatchlistRequest request) {
        log.info("Usuário id={} tentando adicionar filme id={} à lista de desejos", userId, request.movieId());

        if (watchlistItemRepository.existsByUserIdAndMovieId(userId, request.movieId())) {
            log.warn("Usuário id={} já tem o filme id={} na lista de desejos", userId, request.movieId());
            throw new IllegalArgumentException("Este filme já está na sua lista de desejos");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));

        WatchlistItem watchlistItem = WatchlistItem.builder()
                .user(user)
                .movie(movie)
                .build();

        WatchlistItem saved = watchlistItemRepository.save(watchlistItem);

        log.info("Filme id={} adicionado à lista de desejos do usuário id={}", request.movieId(), userId);

        return watchlistItemMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<WatchlistItemResponse> getMyWatchlist(Long userId, Pageable pageable) {
        log.info("Listando lista de desejos do usuário id={}", userId);

        return watchlistItemRepository.findByUserId(userId, pageable)
                .map(watchlistItemMapper::toDto);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long movieId) {
        log.info("Usuário id={} removendo filme id={} da lista de desejos", userId, movieId);

        WatchlistItem watchlistItem = watchlistItemRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado na sua lista de desejos"));

        watchlistItemRepository.delete(watchlistItem);

        log.info("Filme id={} removido da lista de desejos do usuário id={}", movieId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isInWatchlist(Long userId, Long movieId) {
        log.debug("Verificando se filme id={} está na lista de desejos do usuário id={}", movieId, userId);
        return watchlistItemRepository.existsByUserIdAndMovieId(userId, movieId);
    }
}