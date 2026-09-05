package br.com.cinecrew.cinecrew.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class TmdbClient {

    private final RestClient tmdbRestClient;

    public TmdbSearchResponse searchMovies(String query, int page) {
        log.info("Buscando filmes no TMDB com query='{}', page={}", query, page);

        URI uri = UriComponentsBuilder.fromPath("/search/movie")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("language", "pt-BR")
                .build()
                .toUri();

        return tmdbRestClient.get()
                .uri(uri)
                .retrieve()
                .body(TmdbSearchResponse.class);
    }

    @Cacheable(value = "tmdb-movie-details", key = "#tmdbId")
    public TmdbMovieDetails getMoviesDetails(Long tmdbId) {
        log.info("Buscando detalhes do filme tmdb={} no TMDB", tmdbId);

        URI uri = UriComponentsBuilder.fromPath("/movie/{id}")
                .queryParam("language", "pt-BR")
                .buildAndExpand(tmdbId)
                .toUri();

        return tmdbRestClient.get()
                .uri(uri)
                .retrieve()
                .body(TmdbMovieDetails.class);
    }
}
