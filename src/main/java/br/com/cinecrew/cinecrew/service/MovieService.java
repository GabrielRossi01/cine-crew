package br.com.cinecrew.cinecrew.service;


import br.com.cinecrew.cinecrew.client.TmdbClient;
import br.com.cinecrew.cinecrew.client.TmdbMovieDetails;
import br.com.cinecrew.cinecrew.client.TmdbMovieResult;
import br.com.cinecrew.cinecrew.config.TmdbProperties;
import br.com.cinecrew.cinecrew.dto.response.MovieSearchResultResponse;
import br.com.cinecrew.cinecrew.dto.response.MovieResponse;
import br.com.cinecrew.cinecrew.model.Movie;
import br.com.cinecrew.cinecrew.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;
    private final TmdbProperties tmdbProperties;

    public List<MovieSearchResultResponse> searchMovies(String query, int page) {
        var response = tmdbClient.searchMovies(query, page);

        return response.results().stream()
                .map(this::toSearchResultDto)
                .toList();
    }

    @Transactional
    public MovieResponse importMovie(Long tmdbId) {
        log.info("Importando filme tmdbId={} da API do TMDB", tmdbId);

        return movieRepository.findByTmdbId(tmdbId)
                .map(this::toResponse)
                .orElseGet(() -> {
                    TmdbMovieDetails details = tmdbClient.getMoviesDetails(tmdbId);
                    Movie saved = movieRepository.save(toEntity(details));
                    log.info("Filme id={} tmdbId={} importado com sucesso", saved.getId(), tmdbId);
                    return toResponse(saved);
                });
    }

    private MovieSearchResultResponse toSearchResultDto(TmdbMovieResult result) {
        return new MovieSearchResultResponse(
                result.id(),
                result.title(),
                buildImageUrl(result.posterPath()),
                result.releaseDate()
        );
    }

    private Movie toEntity(TmdbMovieDetails details) {
        return Movie.builder()
                .tmdbId(details.id())
                .title(details.title())
                .posterUrl(buildImageUrl(details.posterPath()))
                .releaseYear(extractYear(details.releaseDate()))
                .build();
    }

    private MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getReleaseYear()
        );
    }

    private String buildImageUrl(String posterPath) {
        if (posterPath == null || posterPath.isBlank()) {
            return null;
        }
        return tmdbProperties.imageBaseUrl() + posterPath;
    }

    private Short extractYear(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }
        return (short) LocalDate.parse(releaseDate).getYear();
    }
}